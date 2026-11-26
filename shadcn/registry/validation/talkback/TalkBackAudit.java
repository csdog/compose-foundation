package com.toutiaoyan.compose.talkbackaudit;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class TalkBackAudit extends Instrumentation {
    private static final String CATALOG = "com.toutiaoyan.compose.ui";
    private final List<String> events = new ArrayList<>();
    private Bundle arguments;

    @Override public void onCreate(Bundle arguments) {
        this.arguments = arguments;
        start();
    }

    @Override public void onStart() {
        Bundle report = new Bundle();
        try {
            Context context = getTargetContext();
            AccessibilityManager manager = context.getSystemService(AccessibilityManager.class);
            UiAutomation automation = getUiAutomation(UiAutomation.FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES);
            boolean running = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN)
                .stream().anyMatch(service -> service.getId().contains("com.google.android.marvin.talkback"));
            require(running && manager.isTouchExplorationEnabled(), "TalkBack touch exploration is inactive");
            AccessibilityServiceInfo info = automation.getServiceInfo();
            info.flags |= AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
            automation.setServiceInfo(info);
            automation.setOnAccessibilityEventListener(event -> {
                if (CATALOG.contentEquals(event.getPackageName() == null ? "" : event.getPackageName())) {
                    int kind = event.getEventType();
                    if (kind == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED ||
                            kind == AccessibilityEvent.TYPE_VIEW_CLICKED ||
                            kind == AccessibilityEvent.TYPE_VIEW_SELECTED) {
                        synchronized (events) {
                            events.add(AccessibilityEvent.eventTypeToString(kind) + " " + event.getText()
                                + " " + event.getContentDescription() + " " + summarize(event.getSource(), 3));
                        }
                    }
                }
            });

            String targetLabel = arguments.getString("label");
            String expectedClass = arguments.getString("class");
            boolean checkable = Boolean.parseBoolean(arguments.getString("requireCheckable"));
            boolean clickable = Boolean.parseBoolean(arguments.getString("requireClickable"));
            boolean exactFocus = Boolean.parseBoolean(arguments.getString("exactFocus"));
            boolean focusOnly = Boolean.parseBoolean(arguments.getString("focusOnly"));
            AccessibilityNodeInfo root = automation.getRootInActiveWindow();
            require(root != null && CATALOG.contentEquals(root.getPackageName()), "Catalog is not the active window");
            boolean editing = arguments.containsKey("expectedEditText");
            AccessibilityNodeInfo target = editing
                ? findInCatalog(automation, node -> node.isEditable() && matchesFocus(node, targetLabel, expectedClass, false))
                : findInCatalog(automation, targetLabel, expectedClass, checkable);
            if (target == null && !editing) target = findInCatalog(automation, targetLabel, null, checkable);
            require(target != null, "No matching node: " + targetLabel);
            Rect bounds = new Rect();
            target.getBoundsInScreen(bounds);
            report.putString("target", describe(target) + " bounds=" + bounds);
            Bundle explore = new Bundle();
            explore.putString("explore", bounds.centerX() + " " + bounds.centerY());
            sendStatus(1, explore);
            SystemClock.sleep(1300);
            AccessibilityNodeInfo focused = automation.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY);
            report.putString("afterExplore", summarize(focused, 3));
            if ((focusOnly || editing) && !matchesControl(focused, targetLabel, expectedClass, checkable, clickable, exactFocus)) {
                requestSnapshot("explored");
            }
            Rect viewport = new Rect();
            root.getBoundsInScreen(viewport);
            int start = viewport.left + viewport.width() / 4;
            int end = viewport.left + viewport.width() * 3 / 5;
            int y = viewport.top + viewport.height() / 4;
            int swipes = 0;
            while (swipes < 24 && !matchesControl(focused, targetLabel, expectedClass, checkable, clickable, exactFocus)) {
                Bundle advance = new Bundle();
                advance.putString("next", start + " " + y + " " + end);
                sendStatus(3, advance);
                SystemClock.sleep(720);
                focused = automation.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY);
                swipes++;
            }
            report.putInt("focusSwipes", swipes);
            report.putString("beforeActivate", summarize(focused, 3));
            require(matchesControl(focused, targetLabel, expectedClass, checkable, clickable, exactFocus),
                "TalkBack did not focus the requested control");
            String checkedState = arguments.getString("focusedChecked");
            if (checkedState != null) {
                require(focused.isChecked() == Boolean.parseBoolean(checkedState),
                    "The focused control did not have checked=" + checkedState);
            }
            if (Build.VERSION.SDK_INT >= 30) {
                report.putString("focusedState", value(focused.getStateDescription()));
            }
            AccessibilityNodeInfo.RangeInfo range = focused.getRangeInfo();
            if (range != null) {
                report.putString("focusedRange", range.getMin() + " " + range.getCurrent() + " " + range.getMax());
            }
            if (arguments.containsKey("rowIndex") || arguments.containsKey("columnIndex")) {
                AccessibilityNodeInfo.CollectionItemInfo position = focused.getCollectionItemInfo();
                require(position != null, "Focused control has no collection position");
                report.putString("position", position.getRowIndex() + " " + position.getColumnIndex());
                require(position.getRowIndex() == Integer.parseInt(arguments.getString("rowIndex", "0"))
                    && position.getColumnIndex() == Integer.parseInt(arguments.getString("columnIndex", "0")),
                    "Focused control has the wrong collection position");
            }
            requestSnapshot("focused");
            if (Boolean.parseBoolean(arguments.getString("adjustRange"))) {
                verifyRangeAdjustment(automation, report, focused, viewport);
            } else if (editing) {
                verifyEditing(automation, report, focused);
            } else if (!focusOnly) {
                verifyActivation(automation, report, focused, targetLabel, bounds.centerY());
            }
            report.putString("status", "PASS");
        } catch (Throwable failure) {
            report.putString("status", "FAIL " + failure);
        }
        synchronized (events) {
            report.putString("events", String.join("\n", events));
        }
        finish("PASS".equals(report.getString("status")) ? Activity.RESULT_OK : Activity.RESULT_CANCELED, report);
    }

    private void verifyRangeAdjustment(UiAutomation automation, Bundle report, AccessibilityNodeInfo focused,
            Rect viewport) {
        float initial = rangeValue(focused);
        report.putFloat("rangeInitial", initial);
        int x = viewport.left + viewport.width() / 4;
        int upper = viewport.top + viewport.height() / 3;
        int lower = viewport.top + viewport.height() / 2;
        requestRangeSwipe(x, lower, upper);
        float increased = rangeValue(automation.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY));
        report.putFloat("rangeIncreased", increased);
        require(increased > initial, "TalkBack swipe up did not increase the focused range");
        requestSnapshot("increased");
        requestRangeSwipe(x, upper, lower);
        float decreased = rangeValue(automation.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY));
        report.putFloat("rangeDecreased", decreased);
        require(decreased < increased, "TalkBack swipe down did not decrease the focused range");
        requestSnapshot("decreased");
    }

    private void requestRangeSwipe(int x, int fromY, int toY) {
        Bundle swipe = new Bundle();
        swipe.putString("adjust", x + " " + fromY + " " + toY);
        sendStatus(5, swipe);
        SystemClock.sleep(1400);
    }

    private static float rangeValue(AccessibilityNodeInfo node) {
        require(node != null && node.getRangeInfo() != null, "The focused node has no range information");
        return node.getRangeInfo().getCurrent();
    }

    private void verifyActivation(UiAutomation automation, Bundle report, AccessibilityNodeInfo focused,
            String label, int y) {
        boolean wasChecked = focused.isChecked();
        String stateLabel = arguments.getString("stateLabel");
        String expectedState = arguments.getString("stateAfter");
        if (stateLabel != null) {
            require(Build.VERSION.SDK_INT >= 30, "State descriptions require Android 11 or later");
            boolean unchanged = Boolean.parseBoolean(arguments.getString("stateUnchanged"));
            require(hasState(automation, stateLabel, expectedState) == unchanged,
                "Unexpected initial state for " + stateLabel);
        }
        boolean longPress = Boolean.parseBoolean(arguments.getString("longPress"));
        if (longPress) {
            require(focused.isLongClickable(), "The focused control has no accessibility long press action");
            require(findInCatalog(automation, arguments.getString("shownAfter"), null) == null,
                "The long-press result was already shown");
            Rect bounds = new Rect();
            focused.getBoundsInScreen(bounds);
            requestGesture("hold", bounds.centerX(), bounds.centerY(), 2200);
        } else {
            requestGesture("activate", 40, y, 1500);
        }
        AccessibilityNodeInfo after = automation.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY);
        report.putString("afterActivate", summarize(after, 3));
        requestSnapshot("activated");
        if (!longPress) {
            synchronized (events) {
                require(events.stream().anyMatch(e -> e.startsWith("TYPE_VIEW_CLICKED") && e.contains(label)),
                    "No activation event for focused control");
            }
        }
        if (Boolean.parseBoolean(arguments.getString("closesOptionList"))) {
            require(focused.isCheckable() && findInCatalog(automation, label, null, true) == null,
                "Activation did not remove the checkable option " + label);
        } else if (focused.isCheckable()) {
            require(matchesFocus(after, label, null, true) && after.isChecked() != wasChecked,
                "The focused checkable control did not change state");
        }
        String shownAfter = arguments.getString("shownAfter");
        if (shownAfter != null) {
            require(findInCatalog(automation, shownAfter, null) != null, "Activation did not show " + shownAfter);
        }
        String hiddenAfter = arguments.getString("hiddenAfter");
        if (hiddenAfter != null) {
            require(findInCatalog(automation, hiddenAfter, null) == null, "Activation did not hide " + hiddenAfter);
        }
        String disabledAfter = arguments.getString("disabledAfter");
        if (disabledAfter != null) {
            AccessibilityNodeInfo control = findInCatalog(automation, disabledAfter, null);
            while (control != null && !control.isClickable()) control = control.getParent();
            require(control != null && !control.isEnabled(), "Activation did not disable " + disabledAfter);
            report.putString("disabledAfter", disabledAfter);
        }
        if (stateLabel != null) {
            require(hasState(automation, stateLabel, expectedState), "Activation did not set " + stateLabel + " to " + expectedState);
            report.putString("stateAfter", stateLabel + " = " + expectedState);
        }
    }

    private static boolean hasState(UiAutomation automation, String label, String state) {
        return findInCatalog(automation, node ->
            state.contentEquals(value(node.getStateDescription())) && matchesFocus(node, label, null, false)) != null;
    }

    private void requestGesture(String gesture, int x, int y, int settleMillis) {
        Bundle activate = new Bundle();
        activate.putString(gesture, x + " " + y);
        sendStatus(2, activate);
        SystemClock.sleep(settleMillis);
    }

    private void verifyEditing(UiAutomation automation, Bundle report, AccessibilityNodeInfo target) {
        require(target.isEditable(), "TalkBack did not focus an editable control");
        Rect bounds = new Rect();
        target.getBoundsInScreen(bounds);
        requestGesture("activate", bounds.centerX(), bounds.centerY(), 1500);
        AccessibilityNodeInfo editor = automation.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        report.putString("editorAfterDoubleTap", describe(editor));
        require(editor != null && editor.equals(target), "Double tap did not give input focus to the editor");
        Bundle edit = new Bundle();
        edit.putString("edit", "replace");
        sendStatus(6, edit);
        String expected = arguments.getString("expectedEditText");
        boolean masked = Boolean.parseBoolean(arguments.getString("masked"));
        for (int attempt = 0; attempt < 30; attempt++) {
            AccessibilityNodeInfo updated = automation.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
            if (updated != null && updated.equals(editor)) {
                String value = value(updated.getText());
                boolean matches = masked
                    ? updated.isPassword() && value.length() == expected.length()
                        && value.chars().allMatch(c -> c == '\u2022' || c == '\u25cf' || c == '*')
                    : value.equals(expected);
                if (matches) {
                    report.putString("editedValue", masked ? "masked (" + value.length() + " characters)" : value);
                    requestSnapshot("edited");
                    return;
                }
            }
            SystemClock.sleep(100);
        }
        throw new AssertionError("The focused editor did not expose the expected value");
    }

    private void requestSnapshot(String name) {
        Bundle snapshot = new Bundle();
        snapshot.putString("snapshot", name);
        sendStatus(4, snapshot);
    }

    private static AccessibilityNodeInfo findInCatalog(UiAutomation automation, String text, String className) {
        return findInCatalog(automation, text, className, false);
    }

    private static AccessibilityNodeInfo findInCatalog(UiAutomation automation, String text, String className,
            boolean checkable) {
        return findInCatalog(automation, node -> checkable
            ? matchesFocus(node, text, className, true) : matches(node, text, className));
    }

    private static AccessibilityNodeInfo findInCatalog(UiAutomation automation, Predicate<AccessibilityNodeInfo> predicate) {
        AccessibilityNodeInfo active = automation.getRootInActiveWindow();
        if (active != null && CATALOG.contentEquals(value(active.getPackageName()))) {
            AccessibilityNodeInfo match = find(active, predicate);
            if (match != null) return match;
        }
        for (AccessibilityWindowInfo window : automation.getWindows()) {
            AccessibilityNodeInfo root = window.getRoot();
            if (root != null && CATALOG.contentEquals(value(root.getPackageName()))) {
                AccessibilityNodeInfo match = find(root, predicate);
                if (match != null) return match;
            }
        }
        return null;
    }

    private static AccessibilityNodeInfo find(AccessibilityNodeInfo node, Predicate<AccessibilityNodeInfo> predicate) {
        if (node == null || !node.isVisibleToUser()) return null;
        if (predicate.test(node)) return node;
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo match = find(node.getChild(i), predicate);
            if (match != null) return match;
        }
        return null;
    }

    private static boolean matches(AccessibilityNodeInfo node, String text, String className) {
        return node != null &&
            (text.contentEquals(value(node.getText())) || text.contentEquals(value(node.getContentDescription()))) &&
            (className == null || className.contentEquals(value(node.getClassName())));
    }

    private static boolean matchesFocus(AccessibilityNodeInfo node, String text, String role, boolean checkable) {
        return node != null && (!checkable || node.isCheckable())
            && find(node, child -> matches(child, text, null)) != null && (role == null || hasRole(node, role, 3));
    }

    private static boolean matchesControl(AccessibilityNodeInfo node, String text, String role, boolean checkable,
            boolean clickable, boolean exactFocus) {
        return node != null && (!clickable || node.isClickable()) && (!checkable || node.isCheckable())
            && (exactFocus ? matches(node, text, role) : matchesFocus(node, text, role, false));
    }

    private static String value(CharSequence value) { return value == null ? "" : value.toString(); }

    private static String describe(AccessibilityNodeInfo node) {
        if (node == null) return "(none)";
        return "class=" + node.getClassName() + " text=" + node.getText() + " description=" + node.getContentDescription() +
            " checkable=" + node.isCheckable() + " checked=" + node.isChecked() +
            " clickable=" + node.isClickable() + " enabled=" + node.isEnabled() +
            " accessibilityFocused=" + node.isAccessibilityFocused();
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static boolean hasRole(AccessibilityNodeInfo node, String role, int depth) {
        if (node == null) return false;
        if (role.contentEquals(node.getClassName())) return true;
        for (int i = 0; depth > 0 && i < node.getChildCount(); i++) {
            if (hasRole(node.getChild(i), role, depth - 1)) return true;
        }
        return false;
    }

    private static String summarize(AccessibilityNodeInfo node, int depth) {
        StringBuilder result = new StringBuilder(describe(node));
        if (node != null) {
            for (int i = 0; depth > 0 && i < node.getChildCount(); i++) {
                result.append(" [").append(summarize(node.getChild(i), depth - 1)).append("]");
            }
        }
        return result.toString();
    }
}
