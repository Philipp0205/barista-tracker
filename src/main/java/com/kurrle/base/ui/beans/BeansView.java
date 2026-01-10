package com.kurrle.base.ui.beans;

import com.kurrle.base.ui.ViewToolbar;
import com.kurrle.coffee.CoffeeBean;
import com.kurrle.coffee.CoffeeBeanService;
import com.kurrle.coffee.RoastLevel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

import java.util.Optional;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("beans")
@PageTitle("Coffee Beans")
@Menu(order = 1, icon = "vaadin:coffee", title = "Beans")
@PermitAll
public class BeansView extends VerticalLayout {

    private final CoffeeBeanService coffeeBeanService;
    private final Grid<CoffeeBean> beanGrid;

    public BeansView(CoffeeBeanService coffeeBeanService) {
        this.coffeeBeanService = coffeeBeanService;

        var addButton = new Button("Add Bean", VaadinIcon.PLUS.create(), event -> openBeanDialog(null));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        beanGrid = new Grid<>();
        beanGrid.setItems(query -> coffeeBeanService.listActive(toSpringPageRequest(query)).stream());
        beanGrid.addColumn(CoffeeBean::getName).setHeader("Name").setFlexGrow(2).setSortable(true);
        beanGrid.addColumn(CoffeeBean::getOrigin).setHeader("Origin").setFlexGrow(1);
        beanGrid.addColumn(bean -> bean.getRoastLevel().getDisplayName()).setHeader("Roast Level").setAutoWidth(true);
        beanGrid.addColumn(CoffeeBean::getFlavorNotes).setHeader("Flavor Notes").setFlexGrow(2);
        beanGrid.addComponentColumn(this::createActionButtons).setHeader("Actions").setAutoWidth(true);
        beanGrid.setEmptyStateText("No coffee beans yet. Add your first bean!");
        beanGrid.setSizeFull();
        beanGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().setOverflow(Style.Overflow.HIDDEN);

        add(new ViewToolbar("Coffee Beans", addButton));
        add(beanGrid);
    }

    private HorizontalLayout createActionButtons(CoffeeBean bean) {
        var editButton = new Button(VaadinIcon.EDIT.create(), event -> openBeanDialog(bean));
        editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        editButton.setAriaLabel("Edit bean");

        var deleteButton = new Button(VaadinIcon.TRASH.create(), event -> confirmDelete(bean));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        deleteButton.setAriaLabel("Delete bean");

        var actions = new HorizontalLayout(editButton, deleteButton);
        actions.setSpacing(false);
        return actions;
    }

    private void openBeanDialog(CoffeeBean existingBean) {
        var dialog = new Dialog();
        dialog.setHeaderTitle(existingBean == null ? "Add Coffee Bean" : "Edit Coffee Bean");
        dialog.setCloseOnOutsideClick(false);
        dialog.setWidth("min(90vw, 500px)");

        var nameField = new TextField("Name");
        nameField.setRequired(true);
        nameField.setMaxLength(CoffeeBean.NAME_MAX_LENGTH);
        nameField.setWidthFull();

        var originField = new TextField("Origin");
        originField.setMaxLength(CoffeeBean.ORIGIN_MAX_LENGTH);
        originField.setWidthFull();

        var roastLevelField = new ComboBox<RoastLevel>("Roast Level");
        roastLevelField.setItems(RoastLevel.values());
        roastLevelField.setItemLabelGenerator(RoastLevel::getDisplayName);
        roastLevelField.setRequired(true);
        roastLevelField.setValue(RoastLevel.MEDIUM);
        roastLevelField.setWidthFull();

        var flavorNotesField = new TextArea("Flavor Notes");
        flavorNotesField.setMaxLength(CoffeeBean.FLAVOR_NOTES_MAX_LENGTH);
        flavorNotesField.setPlaceholder("e.g., Chocolate, fruity, nutty...");
        flavorNotesField.setWidthFull();

        if (existingBean != null) {
            nameField.setValue(existingBean.getName());
            originField.setValue(Optional.ofNullable(existingBean.getOrigin()).orElse(""));
            roastLevelField.setValue(existingBean.getRoastLevel());
            flavorNotesField.setValue(Optional.ofNullable(existingBean.getFlavorNotes()).orElse(""));
        }

        var formLayout = new FormLayout(nameField, originField, roastLevelField, flavorNotesField);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("400px", 2)
        );
        formLayout.setColspan(flavorNotesField, 2);

        var cancelButton = new Button("Cancel", event -> dialog.close());

        var saveButton = new Button(existingBean == null ? "Add" : "Save", event -> {
            if (nameField.isEmpty()) {
                nameField.setInvalid(true);
                nameField.setErrorMessage("Name is required");
                return;
            }
            if (roastLevelField.isEmpty()) {
                roastLevelField.setInvalid(true);
                roastLevelField.setErrorMessage("Roast level is required");
                return;
            }

            String origin = originField.isEmpty() ? null : originField.getValue();
            String flavorNotes = flavorNotesField.isEmpty() ? null : flavorNotesField.getValue();

            if (existingBean == null) {
                coffeeBeanService.createBean(nameField.getValue(), roastLevelField.getValue(), origin, flavorNotes);
                Notification.show("Bean added!", 3000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                coffeeBeanService.updateBean(existingBean.getId(), nameField.getValue(), roastLevelField.getValue(), origin, flavorNotes);
                Notification.show("Bean updated!", 3000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }

            beanGrid.getDataProvider().refreshAll();
            dialog.close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(formLayout);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void confirmDelete(CoffeeBean bean) {
        var confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Delete Bean");
        confirmDialog.setText(new Span("Are you sure you want to delete \"" + bean.getName() + "\"?"));
        confirmDialog.setCancelable(true);
        confirmDialog.setConfirmText("Delete");
        confirmDialog.setConfirmButtonTheme("error primary");
        confirmDialog.addConfirmListener(event -> {
            coffeeBeanService.deleteBean(bean.getId());
            beanGrid.getDataProvider().refreshAll();
            Notification.show("Bean deleted", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        });
        confirmDialog.open();
    }
}