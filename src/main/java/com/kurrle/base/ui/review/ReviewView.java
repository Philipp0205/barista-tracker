package com.kurrle.base.ui.review;

import com.kurrle.base.ui.ViewToolbar;
import com.kurrle.coffee.CoffeeBean;
import com.kurrle.coffee.EspressoShot;
import com.kurrle.coffee.EspressoShotService;
import com.kurrle.coffee.TasteProfile;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

import java.util.Optional;

@Route("review/:shotId")
@PageTitle("Review Shot")
@PermitAll
public class ReviewView extends VerticalLayout implements BeforeEnterObserver {

    private final EspressoShotService shotService;
    
    private EspressoShot currentShot;
    private final Div shotInfoPanel;
    private final RadioButtonGroup<TasteProfile> tasteProfileGroup;
    private final TextArea notesField;
    private final Button saveButton;
    private final Button backButton;

    public ReviewView(EspressoShotService shotService) {
        this.shotService = shotService;

        // Shot info panel
        shotInfoPanel = new Div();
        shotInfoPanel.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.MEDIUM
        );

        // Compass image
        var compassImage = new Image("images/Dialing-In-Espresso-Compass.webp", "Espresso Dial-In Compass");
        compassImage.setMaxWidth("400px");
        compassImage.getStyle().set("display", "block");
        compassImage.getStyle().set("margin", "0 auto");

        var compassContainer = new Div(compassImage);
        compassContainer.setWidthFull();
        compassContainer.getStyle().set("text-align", "center");

        // Taste profile selection
        var profileHeader = new H3("How does your shot taste?");
        profileHeader.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.Margin.Bottom.SMALL);

        tasteProfileGroup = new RadioButtonGroup<>();
        tasteProfileGroup.setItems(TasteProfile.values());
        tasteProfileGroup.setItemLabelGenerator(TasteProfile::getDisplayName);
        tasteProfileGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        tasteProfileGroup.setWidthFull();

        // Style the radio group for better compass-like layout
        var profileGrid = new Div();
        profileGrid.addClassNames(
                LumoUtility.Display.GRID,
                LumoUtility.Gap.SMALL
        );
        profileGrid.getStyle().set("grid-template-columns", "repeat(auto-fit, minmax(200px, 1fr))");

        // Create clickable cards for each taste profile
        var tasteCards = createTasteProfileCards();

        // Notes field
        notesField = new TextArea("Additional Notes");
        notesField.setPlaceholder("Any other observations about this shot...");
        notesField.setMaxLength(500);
        notesField.setWidthFull();
        notesField.setMaxHeight("100px");

        // Buttons
        backButton = new Button("Back", VaadinIcon.ARROW_LEFT.create(), 
                event -> UI.getCurrent().navigate(""));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        saveButton = new Button("Save Review", VaadinIcon.CHECK.create(), event -> saveReview());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var buttonLayout = new HorizontalLayout(backButton, saveButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        buttonLayout.setWidthFull();

        // Main content layout
        var contentLayout = new VerticalLayout();
        contentLayout.setMaxWidth("800px");
        contentLayout.setPadding(true);
        contentLayout.setSpacing(true);
        contentLayout.add(shotInfoPanel, compassContainer, profileHeader, tasteCards, notesField, buttonLayout);

        // Page layout
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().setOverflow(Style.Overflow.AUTO);

        add(new ViewToolbar("Review Shot", backButton));
        
        var mainContainer = new VerticalLayout(contentLayout);
        mainContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContainer.setSizeFull();
        add(mainContainer);
    }

    private Div createTasteProfileCards() {
        var container = new Div();
        container.addClassNames(
                LumoUtility.Display.GRID,
                LumoUtility.Gap.SMALL
        );
        container.getStyle().set("grid-template-columns", "repeat(auto-fit, minmax(180px, 1fr))");

        for (TasteProfile profile : TasteProfile.values()) {
            var card = createTasteCard(profile);
            container.add(card);
        }

        return container;
    }

    private Div createTasteCard(TasteProfile profile) {
        var card = new Div();
        card.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.JustifyContent.CENTER
        );
        card.getStyle().set("cursor", "pointer");
        card.getStyle().set("min-height", "80px");
        card.getStyle().set("transition", "all 0.2s ease");
        card.getStyle().set("border", "2px solid transparent");

        // Icon based on profile characteristics
        var icon = getProfileIcon(profile);
        icon.setSize("24px");
        icon.getStyle().set("margin-bottom", "0.5em");

        var label = new Span(profile.getDisplayName());
        label.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextAlignment.CENTER);

        card.add(icon, label);

        // Click handler
        card.addClickListener(event -> {
            tasteProfileGroup.setValue(profile);
            updateCardSelection(card);
        });

        // Hover effect
        card.getElement().addEventListener("mouseenter", e -> {
            if (tasteProfileGroup.getValue() != profile) {
                card.getStyle().set("background-color", "var(--lumo-contrast-10pct)");
            }
        });
        card.getElement().addEventListener("mouseleave", e -> {
            if (tasteProfileGroup.getValue() != profile) {
                card.getStyle().set("background-color", "var(--lumo-contrast-5pct)");
            }
        });

        // Store reference for selection styling
        card.getElement().setProperty("tasteProfile", profile.name());

        return card;
    }

    private void updateCardSelection(Div selectedCard) {
        // Reset all cards
        selectedCard.getParent().ifPresent(parent -> {
            if (parent instanceof Div container) {
                container.getChildren().forEach(child -> {
                    if (child instanceof Div card) {
                        card.getStyle().set("border", "2px solid transparent");
                        card.getStyle().set("background-color", "var(--lumo-contrast-5pct)");
                    }
                });
            }
        });
        
        // Highlight selected
        selectedCard.getStyle().set("border", "2px solid var(--lumo-primary-color)");
        selectedCard.getStyle().set("background-color", "var(--lumo-primary-color-10pct)");
    }

    private com.vaadin.flow.component.icon.Icon getProfileIcon(TasteProfile profile) {
        return switch (profile) {
            case SOUR -> VaadinIcon.ARROW_UP.create(); // Increase yield
            case BITTER -> VaadinIcon.ARROW_DOWN.create(); // Decrease yield
            case MUDDY -> VaadinIcon.ARROW_RIGHT.create(); // Grind coarser
            case WATERY -> VaadinIcon.ARROW_LEFT.create(); // Grind finer
            case MUDDY_SOUR -> VaadinIcon.ARROW_CIRCLE_UP_O.create();
            case MUDDY_BITTER -> VaadinIcon.ARROW_CIRCLE_DOWN_O.create();
            case WATERY_BITTER -> VaadinIcon.ARROW_CIRCLE_LEFT.create();
            case WATERY_SOUR -> VaadinIcon.ARROW_CIRCLE_RIGHT.create();
            case BALANCED -> VaadinIcon.CHECK_CIRCLE.create();
        };
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var shotIdParam = event.getRouteParameters().get("shotId");
        if (shotIdParam.isEmpty()) {
            event.forwardTo("");
            return;
        }

        try {
            var shotId = Long.parseLong(shotIdParam.get());
            var shotOpt = shotService.findByIdWithDetails(shotId);
            
            if (shotOpt.isEmpty()) {
                Notification.show("Shot not found", 3000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                event.forwardTo("");
                return;
            }

            currentShot = shotOpt.get();
            updateShotInfo();

            // Pre-fill existing review if present
            if (currentShot.getReview() != null) {
                tasteProfileGroup.setValue(currentShot.getReview().getTasteProfile());
                notesField.setValue(Optional.ofNullable(currentShot.getReview().getNotes()).orElse(""));
            }

        } catch (NumberFormatException e) {
            event.forwardTo("");
        }
    }

    private void updateShotInfo() {
        shotInfoPanel.removeAll();

        var header = new H3("Shot Details");
        header.addClassNames(LumoUtility.Margin.NONE);

        var beanName = Optional.ofNullable(currentShot.getCoffeeBean())
                .map(CoffeeBean::getName)
                .orElse("Unknown bean");

        var details = new Paragraph(String.format(
                "Bean: %s | Grind: %.1f | Dose: %.1fg | Yield: %.1fg | Time: %ds | Ratio: 1:%.1f",
                beanName,
                currentShot.getGrindSize(),
                currentShot.getGrindAmount(),
                currentShot.getYield(),
                currentShot.getExtractionTime(),
                currentShot.getBrewRatio()
        ));
        details.addClassNames(LumoUtility.Margin.Top.SMALL, LumoUtility.Margin.Bottom.NONE);

        shotInfoPanel.add(header, details);
    }

    private void saveReview() {
        if (currentShot == null) {
            return;
        }

        if (tasteProfileGroup.isEmpty()) {
            Notification.show("Please select a taste profile", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        var notes = notesField.isEmpty() ? null : notesField.getValue();
        var review = shotService.reviewShot(currentShot.getId(), tasteProfileGroup.getValue(), notes);

        Notification.show("Review saved!", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        // Navigate to recommendations
        UI.getCurrent().navigate("recommendation/" + currentShot.getId());
    }
}