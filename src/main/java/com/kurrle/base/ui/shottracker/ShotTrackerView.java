package com.kurrle.base.ui.shottracker;

import com.kurrle.base.ui.ViewToolbar;
import com.kurrle.coffee.CoffeeBean;
import com.kurrle.coffee.CoffeeBeanService;
import com.kurrle.coffee.EspressoShot;
import com.kurrle.coffee.EspressoShotService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import jakarta.annotation.security.PermitAll;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("")
@PageTitle("Shot Tracker")
@Menu(order = 0, icon = "vaadin:drop", title = "Shot Tracker")
@PermitAll
public class ShotTrackerView extends VerticalLayout {

    private final EspressoShotService shotService;
    private final CoffeeBeanService beanService;
    private final Grid<EspressoShot> shotGrid;

    // Quick entry form fields
    private final ComboBox<CoffeeBean> beanSelect;
    private final NumberField grindSizeField;
    private final NumberField grindAmountField;
    private final NumberField yieldField;
    private final IntegerField timeField;

    public ShotTrackerView(EspressoShotService shotService, CoffeeBeanService beanService) {
        this.shotService = shotService;
        this.beanService = beanService;

        // Quick entry form
        beanSelect = new ComboBox<>("Bean");
        beanSelect.setItems(beanService.listActive());
        beanSelect.setItemLabelGenerator(CoffeeBean::toString);
        beanSelect.setPlaceholder("Select bean...");
        beanSelect.setClearButtonVisible(true);

        grindSizeField = new NumberField("Grind Size");
        grindSizeField.setPlaceholder("e.g., 15");
        grindSizeField.setMin(0);
        grindSizeField.setMax(100);
        grindSizeField.setStep(0.5);
        grindSizeField.setStepButtonsVisible(true);

        grindAmountField = new NumberField("Dose (g)");
        grindAmountField.setPlaceholder("e.g., 18");
        grindAmountField.setMin(0);
        grindAmountField.setMax(50);
        grindAmountField.setStep(0.1);
        grindAmountField.setStepButtonsVisible(true);

        yieldField = new NumberField("Yield (g)");
        yieldField.setPlaceholder("e.g., 36");
        yieldField.setMin(0);
        yieldField.setMax(100);
        yieldField.setStep(0.5);
        yieldField.setStepButtonsVisible(true);

        timeField = new IntegerField("Time (s)");
        timeField.setPlaceholder("e.g., 28");
        timeField.setMin(0);
        timeField.setMax(120);
        timeField.setStepButtonsVisible(true);

        // Chain focus on Enter key for quick data entry
        grindSizeField.getElement().addEventListener("keydown", e -> grindAmountField.focus())
                .setFilter("event.key === 'Enter'");
        grindAmountField.getElement().addEventListener("keydown", e -> yieldField.focus())
                .setFilter("event.key === 'Enter'");
        yieldField.getElement().addEventListener("keydown", e -> timeField.focus())
                .setFilter("event.key === 'Enter'");
        timeField.getElement().addEventListener("keydown", e -> addShot())
                .setFilter("event.key === 'Enter'");

        var addButton = new Button("Add Shot", VaadinIcon.PLUS.create(), event -> addShot());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Form layout for quick entry
        var formLayout = new FormLayout();
        formLayout.add(beanSelect, grindSizeField, grindAmountField, yieldField, timeField);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("300px", 2),
                new FormLayout.ResponsiveStep("500px", 3),
                new FormLayout.ResponsiveStep("700px", 5)
        );

        var formContainer = new HorizontalLayout();
        formContainer.setWidthFull();
        formContainer.setAlignItems(FlexComponent.Alignment.END);
        formContainer.add(formLayout, addButton);
        formContainer.setFlexGrow(1, formLayout);

        // Date formatter for the grid
        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(getLocale())
                .withZone(ZoneId.systemDefault());

        // Shot grid
        shotGrid = new Grid<>();
        shotGrid.setItems(query -> shotService.list(toSpringPageRequest(query)).stream());
        
        shotGrid.addColumn(shot -> Optional.ofNullable(shot.getCoffeeBean())
                        .map(CoffeeBean::getName).orElse("-"))
                .setHeader("Bean")
                .setFlexGrow(2)
                .setSortable(true);
        
        shotGrid.addColumn(EspressoShot::getGrindSize)
                .setHeader("Grind")
                .setAutoWidth(true);
        
        shotGrid.addColumn(shot -> String.format("%.1fg", shot.getGrindAmount()))
                .setHeader("Dose")
                .setAutoWidth(true);
        
        shotGrid.addColumn(shot -> String.format("%.1fg", shot.getYield()))
                .setHeader("Yield")
                .setAutoWidth(true);
        
        shotGrid.addColumn(shot -> shot.getExtractionTime() + "s")
                .setHeader("Time")
                .setAutoWidth(true);
        
        shotGrid.addColumn(shot -> String.format("1:%.1f", shot.getBrewRatio()))
                .setHeader("Ratio")
                .setAutoWidth(true);
        
        shotGrid.addColumn(shot -> dateTimeFormatter.format(shot.getCreatedAt()))
                .setHeader("Date")
                .setAutoWidth(true);
        
        shotGrid.addColumn(shot -> shot.getReview() != null ? "✓" : "")
                .setHeader("Reviewed")
                .setAutoWidth(true);
        
        shotGrid.addComponentColumn(this::createActionButtons)
                .setHeader("Actions")
                .setAutoWidth(true);

        shotGrid.setEmptyStateText("No shots yet. Pull your first shot and track it!");
        shotGrid.setSizeFull();
        shotGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().setOverflow(Style.Overflow.HIDDEN);

        add(new ViewToolbar("Shot Tracker"));
        
        // Quick entry section
        var entrySection = new VerticalLayout(formContainer);
        entrySection.setPadding(true);
        entrySection.setSpacing(false);
        add(entrySection);
        
        add(shotGrid);
    }

    private void addShot() {
        // Validate required fields
        if (grindSizeField.isEmpty()) {
            grindSizeField.setInvalid(true);
            grindSizeField.setErrorMessage("Required");
            grindSizeField.focus();
            return;
        }
        if (grindAmountField.isEmpty()) {
            grindAmountField.setInvalid(true);
            grindAmountField.setErrorMessage("Required");
            grindAmountField.focus();
            return;
        }
        if (yieldField.isEmpty()) {
            yieldField.setInvalid(true);
            yieldField.setErrorMessage("Required");
            yieldField.focus();
            return;
        }
        if (timeField.isEmpty()) {
            timeField.setInvalid(true);
            timeField.setErrorMessage("Required");
            timeField.focus();
            return;
        }

        Long beanId = beanSelect.getValue() != null ? beanSelect.getValue().getId() : null;
        
        var shot = shotService.createShot(
                grindSizeField.getValue(),
                grindAmountField.getValue(),
                yieldField.getValue(),
                timeField.getValue(),
                beanId
        );

        shotGrid.getDataProvider().refreshAll();
        
        // Clear form but keep bean selection for consecutive shots
        grindSizeField.clear();
        grindAmountField.clear();
        yieldField.clear();
        timeField.clear();
        
        // Focus first field for next entry
        grindSizeField.focus();

        Notification.show("Shot tracked! Ready to review?", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        
        // Optionally navigate to review
        navigateToReview(shot.getId());
    }

    private HorizontalLayout createActionButtons(EspressoShot shot) {
        var reviewButton = new Button(VaadinIcon.CHECK_CIRCLE.create(), 
                event -> navigateToReview(shot.getId()));
        reviewButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        reviewButton.setAriaLabel("Review shot");
        reviewButton.getElement().setAttribute("title", 
                shot.getReview() != null ? "Edit review" : "Review shot");

        var deleteButton = new Button(VaadinIcon.TRASH.create(), 
                event -> confirmDelete(shot));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        deleteButton.setAriaLabel("Delete shot");

        var actions = new HorizontalLayout(reviewButton, deleteButton);
        actions.setSpacing(false);
        return actions;
    }

    private void navigateToReview(Long shotId) {
        UI.getCurrent().navigate("review/" + shotId);
    }

    private void confirmDelete(EspressoShot shot) {
        var confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Delete Shot");
        confirmDialog.setText(new Span("Are you sure you want to delete this shot?"));
        confirmDialog.setCancelable(true);
        confirmDialog.setConfirmText("Delete");
        confirmDialog.setConfirmButtonTheme("error primary");
        confirmDialog.addConfirmListener(event -> {
            shotService.deleteShot(shot.getId());
            shotGrid.getDataProvider().refreshAll();
            Notification.show("Shot deleted", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        });
        confirmDialog.open();
    }
}