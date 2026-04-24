package com.urbanissue.util;

import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Auto-complete TextField that shows suggestions as user types
 */
public class AutoCompleteTextField extends TextField {

    private ContextMenu suggestionMenu;
    private Function<String, List<String>> suggestionProvider;
    private int minCharsForSuggestion = 2;
    private volatile String lastQuery = "";

    public AutoCompleteTextField() {
        super();
        setupAutoComplete();
    }

    public AutoCompleteTextField(String text) {
        super(text);
        setupAutoComplete();
    }

    private void setupAutoComplete() {
        suggestionMenu = new ContextMenu();

        // Add listeners for text changes and key events
        textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(lastQuery)) {
                lastQuery = newValue;
                if (newValue.length() >= minCharsForSuggestion) {
                    showSuggestions(newValue);
                } else {
                    hideSuggestions();
                }
            }
        });

        // Handle keyboard navigation
        addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (suggestionMenu.isShowing()) {
                if (event.getCode() == KeyCode.DOWN) {
                    suggestionMenu.requestFocus();
                    event.consume();
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    hideSuggestions();
                    event.consume();
                }
            }
        });

        // Hide suggestions when focus is lost
        focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                Platform.runLater(() -> {
                    if (!suggestionMenu.isFocused()) {
                        hideSuggestions();
                    }
                });
            }
        });
    }

    private void showSuggestions(String query) {
        if (suggestionProvider == null) return;

        // Run suggestion search in background to avoid blocking UI
        CompletableFuture.supplyAsync(() -> suggestionProvider.apply(query))
            .thenAcceptAsync(suggestions -> {
                if (!query.equals(getText()) || suggestions == null || suggestions.isEmpty()) {
                    hideSuggestions();
                    return;
                }

                Platform.runLater(() -> {
                    suggestionMenu.getItems().clear();

                    // Limit to maximum 8 suggestions
                    int maxSuggestions = Math.min(8, suggestions.size());
                    for (int i = 0; i < maxSuggestions; i++) {
                        String suggestion = suggestions.get(i);
                        MenuItem item = new MenuItem(suggestion);
                        item.setOnAction(e -> {
                            setText(suggestion);
                            positionCaret(suggestion.length());
                            hideSuggestions();
                            requestFocus();
                        });
                        suggestionMenu.getItems().add(item);
                    }

                    if (!suggestionMenu.isShowing() && isFocused()) {
                        suggestionMenu.show(this, Side.BOTTOM, 0, 0);
                    }
                });
            })
            .exceptionally(throwable -> {
                System.err.println("Error getting suggestions: " + throwable.getMessage());
                hideSuggestions();
                return null;
            });
    }

    private void hideSuggestions() {
        Platform.runLater(() -> {
            if (suggestionMenu.isShowing()) {
                suggestionMenu.hide();
            }
        });
    }

    /**
     * Set the function that provides suggestions based on input text
     */
    public void setSuggestionProvider(Function<String, List<String>> provider) {
        this.suggestionProvider = provider;
    }

    /**
     * Set minimum characters required before showing suggestions
     */
    public void setMinCharsForSuggestion(int minChars) {
        this.minCharsForSuggestion = minChars;
    }

    @Override
    public void clear() {
        super.clear();
        hideSuggestions();
        lastQuery = "";
    }
}