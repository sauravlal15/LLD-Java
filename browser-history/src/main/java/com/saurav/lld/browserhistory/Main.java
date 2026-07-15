package com.saurav.lld.browserhistory;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Browser History => user search url => move forward or back => tab history =>
 * browser can have multiple tabs => each tab can have search history by url
 *
 * Entry point for browser-history. Add domain types in this package (or
 * subpackages), not in the default package.
 *
 *
 * Browser - Tab - History - HistoryEntry
 */
class HistoryEntry {

    final String url;
    final String title;
    final Instant visitedAt;
    HistoryEntry prev, next;

    public HistoryEntry(String url, String title) {
        this.url = url;
        this.title = title;
        this.visitedAt = Instant.now();
    }

}

class History {

    private HistoryEntry current;
    private HistoryEntry head;

    History() {
    }

    void visit(String url, String title) {
        HistoryEntry e = new HistoryEntry(url, title);
        if (current == null) {
            current = head = e;
            return;
        }

        current.next = e;
        e.prev = current;
        current = e;

    }

    String back(Integer steps) {
        if (steps < 0) {
            throw new IllegalArgumentException("non-negative");
        }
        while (steps > 0 && current != null && current.prev != null) {
            current = current.prev;
            steps--;
        }
        return current != null ? current.url : null;
    }

    String forward(Integer steps) {
        if (steps < 0) {
            throw new IllegalArgumentException("non negative");
        }
        while (steps > 0 && current != null && current.prev != null) {
            current = current.prev;
            steps--;
        }
        return current != null ? current.url : null;
    }

    String currentURL() {
        return current == null ? null : current.url;
    }

    void clear() { // clear all history of that tab
        HistoryEntry n = head;
        while (n != null) {
            HistoryEntry nxt = n.next;
            n.prev = null;
            n.next = null;
            n = nxt;
        }
        head = null;
        current = null;
    }

}

class Tab {

    final String id = UUID.randomUUID().toString().substring(0, 8);
    final History history;

    Tab(String initialURL) {
        this.history = new History();
        if (initialURL != null) {
            this.history.visit(initialURL, "");
        }
    }

    void visit(String url) {
        history.visit(url, "");
    }

    String back(int steps) {
        return history.back(steps);
    }

    String forward(int steps) {
        return history.forward(steps);
    }

    void clearHistory() { // all search is cleared for that tab
        history.clear();
    }

    String currentUrl() {
        return history.currentURL();
    }

}

class Browser {

    Map<String, Tab> tabs = new LinkedHashMap<>();
    String activeTabId;

    String openTab(String url) {
        Tab t = new Tab(url);
        tabs.put(t.id, t);
        activeTabId = t.id;
        return t.id;
    }

    void closeTab(String id) {
        if (!tabs.containsKey(id)) {
            throw new IllegalArgumentException("unknown tab");
        }
        tabs.remove(id);
        if (activeTabId == id) {
            if (tabs.isEmpty()) {
                return;
            }
            tabs.keySet().iterator().next();
        }
    }

    void clearAllHistory() { // clear all history of browser, i.e. all tabs

    }

    Tab active() {
        if (activeTabId == null) {
            return null;
        }
        return tabs.get(activeTabId);
    }

}

public class Main {

    public static void main(String[] args) {
        System.out.println("browser-history ready.");
        Browser b = new Browser();
        String tabId = b.openTab("https://google.com");
        Tab t = b.active();

        t.visit("https://google.com/search?q=lld");
        t.visit("https://crackingwalnuts.com");

        System.out.println("Current Tab: " + t.currentUrl());

        t.back(1);

        System.out.println("Back: " + t.currentUrl());
        t.back(1);
        System.out.println("Back x2: " + t.currentUrl());
        t.forward(2);
        System.out.println("Forward x2: " + t.currentUrl());

        t.back(1);
        t.visit("https://news.ycombinator.com");
        System.out.println("Branched: " + t.currentUrl());

        b.clearAllHistory();
        System.out.println("After clear-all, currentUrl: " + t.currentUrl());
    }
}
