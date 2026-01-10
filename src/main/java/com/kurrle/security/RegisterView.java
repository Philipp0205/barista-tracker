package com.kurrle.security;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route(value = "register")
@PageTitle("Register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    private final UserService userService;
    
    private final TextField firstNameField = new TextField("First Name");
    private final TextField lastNameField = new TextField("Last Name");
    private final EmailField emailField = new EmailField("Email");
    private final PasswordField passwordField = new PasswordField("Password");
    private final PasswordField confirmPasswordField = new PasswordField("Confirm Password");
    private final Button registerButton = new Button("Register");
    private final Button backToLoginButton = new Button("Back to Login");

    private final Binder<RegistrationForm> binder = new Binder<>(RegistrationForm.class);

    public RegisterView(UserService userService) {
        this.userService = userService;
        
        addClassName("register-view");
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Div registerContainer = createRegisterContainer();
        add(registerContainer);
    }

    private Div createRegisterContainer() {
        Div registerContainer = new Div();
        registerContainer.addClassNames(
            LumoUtility.Display.FLEX,
            LumoUtility.FlexDirection.COLUMN,
            LumoUtility.AlignItems.CENTER,
            LumoUtility.Gap.MEDIUM,
            LumoUtility.Padding.LARGE,
            LumoUtility.Background.BASE,
            LumoUtility.BorderRadius.LARGE
        );
        registerContainer.getStyle().set("box-shadow", "var(--lumo-box-shadow-m)");
        registerContainer.getStyle().set("max-width", "450px");
        registerContainer.getStyle().set("width", "100%");

        // Logo/Header
        var appLogo = VaadinIcon.COFFEE.create();
        appLogo.setSize("64px");
        appLogo.setColor("var(--lumo-primary-color)");

        H1 title = new H1("Create Account");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontSize.XLARGE);

        FormLayout formLayout = createFormLayout();
        configureBinder();
        HorizontalLayout buttonLayout = createButtonLayout();
        HorizontalLayout loginSection = createLoginSection();

        registerContainer.add(appLogo, title, formLayout, buttonLayout, loginSection);
        return registerContainer;
    }

    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        formLayout.setWidthFull();

        firstNameField.setRequired(true);
        firstNameField.setWidthFull();
        
        lastNameField.setRequired(true);
        lastNameField.setWidthFull();
        
        emailField.setRequired(true);
        emailField.setWidthFull();
        
        passwordField.setRequired(true);
        passwordField.setWidthFull();
        passwordField.setHelperText("At least 8 characters");
        
        confirmPasswordField.setRequired(true);
        confirmPasswordField.setWidthFull();

        formLayout.add(firstNameField, lastNameField, emailField, passwordField, confirmPasswordField);
        
        return formLayout;
    }

    private void configureBinder() {
        binder.forField(firstNameField)
                .asRequired("First name is required")
                .bind(RegistrationForm::getFirstName, RegistrationForm::setFirstName);

        binder.forField(lastNameField)
                .asRequired("Last name is required")
                .bind(RegistrationForm::getLastName, RegistrationForm::setLastName);

        binder.forField(emailField)
                .asRequired("Email is required")
                .withValidator(new EmailValidator("Please enter a valid email address"))
                .bind(RegistrationForm::getEmail, RegistrationForm::setEmail);

        binder.forField(passwordField)
                .asRequired("Password is required")
                .withValidator(password -> password.length() >= 8, "Password must be at least 8 characters")
                .bind(RegistrationForm::getPassword, RegistrationForm::setPassword);

        binder.forField(confirmPasswordField)
                .asRequired("Please confirm your password")
                .withValidator(confirm -> confirm.equals(passwordField.getValue()), "Passwords do not match")
                .bind(RegistrationForm::getConfirmPassword, RegistrationForm::setConfirmPassword);

        binder.setBean(new RegistrationForm());
    }

    private HorizontalLayout createButtonLayout() {
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidthFull();
        registerButton.addClickListener(e -> handleRegistration());

        HorizontalLayout buttonLayout = new HorizontalLayout(registerButton);
        buttonLayout.setWidthFull();
        return buttonLayout;
    }

    private HorizontalLayout createLoginSection() {
        HorizontalLayout loginSection = new HorizontalLayout();
        loginSection.addClassNames(
            LumoUtility.AlignItems.CENTER,
            LumoUtility.Gap.SMALL
        );
        
        Span loginText = new Span("Already have an account?");
        
        backToLoginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        backToLoginButton.addClickListener(e -> 
            getUI().ifPresent(ui -> ui.navigate(LoginView.class))
        );

        loginSection.add(loginText, backToLoginButton);
        return loginSection;
    }

    private void handleRegistration() {
        if (binder.validate().isOk()) {
            RegistrationForm form = binder.getBean();
            try {
                userService.registerUser(
                    form.getFirstName(),
                    form.getLastName(),
                    form.getEmail(),
                    form.getPassword()
                );
                
                Notification.show("Registration successful! Please log in.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                
                getUI().ifPresent(ui -> ui.navigate(LoginView.class));
            } catch (IllegalArgumentException ex) {
                Notification.show(ex.getMessage(), 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }

    // Inner class for registration form data
    public static class RegistrationForm {
        private String firstName = "";
        private String lastName = "";
        private String email = "";
        private String password = "";
        private String confirmPassword = "";

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}
