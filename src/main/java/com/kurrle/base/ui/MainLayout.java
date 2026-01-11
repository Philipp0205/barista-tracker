package com.kurrle.base.ui;

import com.kurrle.security.AuthenticatedUser;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;

import com.vaadin.flow.server.auth.AnonymousAllowed;

@Layout
@AnonymousAllowed
public final class MainLayout extends AppLayout {

    private final AuthenticatedUser authenticatedUser;

    MainLayout(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setPrimarySection(Section.DRAWER);
        addToDrawer(createHeader(), new Scroller(createSideNav()), createFooter());
    }

    private Component createHeader() {
        var appLogo = VaadinIcon.COFFEE.create();
        appLogo.setSize("48px");
        appLogo.setColor("var(--lumo-primary-color)");

        var appName = new Span("Espresso Dial-In");
        appName.getStyle().setFontWeight(Style.FontWeight.BOLD);

        var header = new VerticalLayout(appLogo, appName);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        return header;
    }

    private SideNav createSideNav() {
        var nav = new SideNav();
        nav.addClassNames(LumoUtility.Margin.Horizontal.MEDIUM);
        MenuConfiguration.getMenuEntries().forEach(entry -> nav.addItem(createSideNavItem(entry)));
        return nav;
    }

    private SideNavItem createSideNavItem(MenuEntry menuEntry) {
        if (menuEntry.icon() != null) {
            return new SideNavItem(menuEntry.title(), menuEntry.path(), new Icon(menuEntry.icon()));
        } else {
            return new SideNavItem(menuEntry.title(), menuEntry.path());
        }
    }

    private Component createFooter() {
        var footer = new VerticalLayout();
        footer.addClassNames(LumoUtility.Padding.MEDIUM);
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        
        authenticatedUser.get().ifPresent(user -> {
            var userInfo = new HorizontalLayout();
            userInfo.setAlignItems(FlexComponent.Alignment.CENTER);
            userInfo.addClassNames(LumoUtility.Gap.SMALL);
            
            var userIcon = VaadinIcon.USER.create();
            var userName = new Span(user.getFullName());
            userName.addClassNames(LumoUtility.FontSize.SMALL);
            userInfo.add(userIcon, userName);
            
            var logoutButton = new Button("Logout", VaadinIcon.SIGN_OUT.create(), e -> authenticatedUser.logout());
            logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            logoutButton.setWidthFull();
            
            footer.add(userInfo, logoutButton);
        });
        
        return footer;
    }
}