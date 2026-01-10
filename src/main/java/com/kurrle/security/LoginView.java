package com.kurrle.security;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route(value = "login")
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Container for login content
        Div loginContainer = new Div();
        loginContainer.addClassNames(
            LumoUtility.Display.FLEX,
            LumoUtility.FlexDirection.COLUMN,
            LumoUtility.AlignItems.CENTER,
            LumoUtility.Gap.MEDIUM,
            LumoUtility.Padding.LARGE,
            LumoUtility.Background.BASE,
            LumoUtility.BorderRadius.LARGE
        );
        loginContainer.getStyle().set("box-shadow", "var(--lumo-box-shadow-m)");
        loginContainer.getStyle().set("max-width", "400px");
        loginContainer.getStyle().set("width", "100%");

        // Logo/Header
        var appLogo = VaadinIcon.COFFEE.create();
        appLogo.setSize("64px");
        appLogo.setColor("var(--lumo-primary-color)");

        H1 title = new H1("Espresso Dial-In");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontSize.XLARGE);

        // Configure login form
        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);

        // Registration section
        HorizontalLayout registerSection = createRegisterSection();

        loginContainer.add(appLogo, title, loginForm, new Hr(), registerSection);
        add(loginContainer);
    }

    private HorizontalLayout createRegisterSection() {
        HorizontalLayout registerSection = new HorizontalLayout();
        registerSection.addClassNames(
            LumoUtility.AlignItems.CENTER,
            LumoUtility.Gap.SMALL
        );
        
        Span registerText = new Span("Don't have an account?");
        
        Button registerButton = new Button("Register here");
        registerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        registerButton.addClickListener(e -> 
            getUI().ifPresent(ui -> ui.navigate(RegisterView.class))
        );

        registerSection.add(registerText, registerButton);
        return registerSection;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true);
        }
    }
}
