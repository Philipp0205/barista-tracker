package com.kurrle.base.ui.recommendation;

import com.kurrle.base.ui.ViewToolbar;
import com.kurrle.coffee.CoffeeBean;
import com.kurrle.coffee.EspressoShot;
import com.kurrle.coffee.EspressoShotService;
import com.kurrle.coffee.TasteProfile;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

import java.util.Optional;

@Route("recommendation/:shotId")
@PageTitle("Dial-In Recommendations")
@PermitAll
public class RecommendationView extends VerticalLayout implements BeforeEnterObserver {

    private final EspressoShotService shotService;
    
    private EspressoShot currentShot;
    private final Div contentContainer;

    public RecommendationView(EspressoShotService shotService) {
        this.shotService = shotService;

        contentContainer = new Div();
        contentContainer.setWidthFull();

        var backButton = new Button("Back to Shots", VaadinIcon.ARROW_LEFT.create(),
                event -> UI.getCurrent().navigate(""));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().setOverflow(Style.Overflow.AUTO);

        add(new ViewToolbar("Dial-In Recommendations", backButton));

        var mainContainer = new VerticalLayout(contentContainer);
        mainContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContainer.setSizeFull();
        mainContainer.setPadding(true);
        add(mainContainer);
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
            
            if (currentShot.getReview() == null) {
                Notification.show("This shot has not been reviewed yet", 3000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
                event.forwardTo("review/" + shotId);
                return;
            }

            buildRecommendationContent();

        } catch (NumberFormatException e) {
            event.forwardTo("");
        }
    }

    private void buildRecommendationContent() {
        contentContainer.removeAll();

        var content = new VerticalLayout();
        content.setMaxWidth("800px");
        content.setPadding(false);
        content.setSpacing(true);

        // Shot info card
        content.add(createShotInfoCard());

        // Review result card
        content.add(createReviewResultCard());

        // Recommendations card
        content.add(createRecommendationsCard());

        // Action buttons
        content.add(createActionButtons());

        contentContainer.add(content);
    }

    private Div createShotInfoCard() {
        var card = new Div();
        card.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.MEDIUM
        );

        var header = new H3("Current Shot Parameters");
        header.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Margin.Bottom.SMALL);

        var beanName = Optional.ofNullable(currentShot.getCoffeeBean())
                .map(CoffeeBean::getName)
                .orElse("Unknown bean");

        var detailsGrid = new Div();
        detailsGrid.addClassNames(LumoUtility.Display.GRID, LumoUtility.Gap.SMALL);
        detailsGrid.getStyle().set("grid-template-columns", "repeat(auto-fit, minmax(150px, 1fr))");

        detailsGrid.add(
                createParameterItem("Bean", beanName, VaadinIcon.COFFEE),
                createParameterItem("Grind Size", String.format("%.1f", currentShot.getGrindSize()), VaadinIcon.COG),
                createParameterItem("Dose", String.format("%.1fg", currentShot.getGrindAmount()), VaadinIcon.SCALE),
                createParameterItem("Yield", String.format("%.1fg", currentShot.getYield()), VaadinIcon.DROP),
                createParameterItem("Time", currentShot.getExtractionTime() + "s", VaadinIcon.TIMER),
                createParameterItem("Ratio", String.format("1:%.1f", currentShot.getBrewRatio()), VaadinIcon.CALC_BOOK)
        );

        card.add(header, detailsGrid);
        return card;
    }

    private Div createParameterItem(String label, String value, VaadinIcon iconType) {
        var item = new Div();
        item.addClassNames(
                LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.Gap.SMALL
        );

        var icon = iconType.create();
        icon.setSize("16px");
        icon.addClassNames(LumoUtility.TextColor.SECONDARY);

        var labelSpan = new Span(label + ": ");
        labelSpan.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        var valueSpan = new Span(value);
        valueSpan.addClassNames(LumoUtility.FontWeight.SEMIBOLD);

        item.add(icon, labelSpan, valueSpan);
        return item;
    }

    private Div createReviewResultCard() {
        var card = new Div();
        card.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.MEDIUM
        );

        var header = new H3("Your Review");
        header.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Margin.Bottom.SMALL);

        var review = currentShot.getReview();
        var profile = review.getTasteProfile();

        var profileBadge = new Span(profile.getDisplayName());
        profileBadge.addClassNames(
                LumoUtility.Background.PRIMARY_10,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.Padding.Vertical.XSMALL,
                LumoUtility.FontWeight.SEMIBOLD
        );

        var profileContainer = new Div(profileBadge);
        profileContainer.addClassNames(LumoUtility.Margin.Bottom.SMALL);

        if (review.getNotes() != null && !review.getNotes().isBlank()) {
            var notesLabel = new Span("Notes: ");
            notesLabel.addClassNames(LumoUtility.TextColor.SECONDARY);
            
            var notes = new Paragraph("\"" + review.getNotes() + "\"");
            notes.addClassNames(LumoUtility.Margin.NONE);
            notes.getStyle().set("font-style", "italic");
            
            card.add(header, profileContainer, notesLabel, notes);
        } else {
            card.add(header, profileContainer);
        }

        return card;
    }

    private Div createRecommendationsCard() {
        var card = new Div();
        card.addClassNames(
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.LARGE
        );

        var profile = currentShot.getReview().getTasteProfile();

        if (profile == TasteProfile.BALANCED) {
            card.addClassNames(LumoUtility.Background.SUCCESS_10);
            card.add(createBalancedMessage());
        } else {
            card.addClassNames(LumoUtility.Background.PRIMARY_10);
            card.add(createAdjustmentRecommendations(profile));
        }

        return card;
    }

    private VerticalLayout createBalancedMessage() {
        var layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        var icon = VaadinIcon.CHECK_CIRCLE.create();
        icon.setSize("48px");
        icon.addClassNames(LumoUtility.TextColor.SUCCESS);

        var header = new H2("Perfect Shot! 🎉");
        header.addClassNames(LumoUtility.Margin.NONE, LumoUtility.TextColor.SUCCESS);

        var message = new Paragraph("Your espresso is well balanced. Keep these parameters for this bean!");
        message.addClassNames(LumoUtility.TextAlignment.CENTER);

        layout.add(icon, header, message);
        return layout;
    }

    private VerticalLayout createAdjustmentRecommendations(TasteProfile profile) {
        var layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(true);

        var header = new H2("Recommended Adjustments");
        header.addClassNames(LumoUtility.Margin.NONE);

        var summary = new Paragraph(profile.getRecommendation());
        summary.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);

        layout.add(header, summary);

        // Detailed adjustments
        var adjustmentsContainer = new VerticalLayout();
        adjustmentsContainer.setPadding(false);
        adjustmentsContainer.setSpacing(false);

        // Yield adjustments
        if (profile.shouldIncreaseYield()) {
            adjustmentsContainer.add(createAdjustmentItem(
                    VaadinIcon.ARROW_UP,
                    "Increase Yield",
                    String.format("Try %.0fg → %.0fg", currentShot.getYield(), currentShot.getYield() + 2),
                    "Higher yield extracts more, reducing sourness"
            ));
        } else if (profile.shouldDecreaseYield()) {
            adjustmentsContainer.add(createAdjustmentItem(
                    VaadinIcon.ARROW_DOWN,
                    "Decrease Yield",
                    String.format("Try %.0fg → %.0fg", currentShot.getYield(), Math.max(currentShot.getYield() - 2, currentShot.getGrindAmount())),
                    "Lower yield reduces over-extraction and bitterness"
            ));
        }

        // Grind adjustments
        if (profile.shouldGrindFiner()) {
            adjustmentsContainer.add(createAdjustmentItem(
                    VaadinIcon.MINUS,
                    "Grind Finer",
                    String.format("Try %.1f → %.1f", currentShot.getGrindSize(), currentShot.getGrindSize() - 0.5),
                    "Finer grind increases extraction and body"
            ));
        } else if (profile.shouldGrindCoarser()) {
            adjustmentsContainer.add(createAdjustmentItem(
                    VaadinIcon.PLUS,
                    "Grind Coarser",
                    String.format("Try %.1f → %.1f", currentShot.getGrindSize(), currentShot.getGrindSize() + 0.5),
                    "Coarser grind reduces muddiness and over-extraction"
            ));
        }

        layout.add(adjustmentsContainer);
        return layout;
    }

    private Div createAdjustmentItem(VaadinIcon iconType, String action, String suggestion, String explanation) {
        var item = new Div();
        item.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Margin.Vertical.XSMALL,
                LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.START,
                LumoUtility.Gap.MEDIUM
        );

        var icon = iconType.create();
        icon.setSize("32px");
        icon.addClassNames(LumoUtility.TextColor.PRIMARY);

        var textContent = new VerticalLayout();
        textContent.setPadding(false);
        textContent.setSpacing(false);

        var actionSpan = new Span(action);
        actionSpan.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.LARGE);

        var suggestionSpan = new Span(suggestion);
        suggestionSpan.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.FontWeight.SEMIBOLD);

        var explanationSpan = new Span(explanation);
        explanationSpan.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        textContent.add(actionSpan, suggestionSpan, explanationSpan);
        item.add(icon, textContent);

        return item;
    }

    private HorizontalLayout createActionButtons() {
        var trackNewShot = new Button("Track New Shot", VaadinIcon.PLUS.create(),
                event -> UI.getCurrent().navigate(""));
        trackNewShot.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var editReview = new Button("Edit Review", VaadinIcon.EDIT.create(),
                event -> UI.getCurrent().navigate("review/" + currentShot.getId()));
        editReview.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        var layout = new HorizontalLayout(editReview, trackNewShot);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.setWidthFull();
        layout.addClassNames(LumoUtility.Margin.Top.LARGE);

        return layout;
    }
}