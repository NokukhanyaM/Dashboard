package org.example.bean;


import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.User;
import org.example.service.AuthClient;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.annotation.PostConstruct;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.IOException;
import java.io.Serializable;


@Setter
@Getter
@Named("dashboardBean")
@SessionScoped
public class DashboardBean implements Serializable {

    private static final Logger log = LogManager.getLogger(DashboardBean.class);
    private AuthClient authClient = new AuthClient();

    private double balance = 100.00;

    private double depositAmount;

    private double withdrawAmount;

    private User currentUser;

    private String currentView = "home";

    @PostConstruct
    public void init() {
        initUser();
    }

    public void initUser() {
        try {
            //Grab the token from the session (stored during login)
            String token = (String) FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get("token");

            if (token == null) {
                javax.servlet.http.HttpServletRequest request =
                        (javax.servlet.http.HttpServletRequest) FacesContext.getCurrentInstance()
                                .getExternalContext().getRequest();

                javax.servlet.http.Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (javax.servlet.http.Cookie c : cookies) {
                        if ("AUTH_TOKEN".equals(c.getName())) {
                            token = c.getValue();
                            // Save it into the JSF session for reuse
                            FacesContext.getCurrentInstance()
                                    .getExternalContext().getSessionMap().put("token", token);
                            break;
                        }
                    }
                }
            }

//            log.info("Token: {}",token);

            if (token != null) {
                //Call your AuthClient to get the full user object
                this.currentUser = authClient.getLoggedInUser(token);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.currentUser = null;
        }
    }


    public void setView(String view) {
        this.currentView = view;
    }

    public void deposit() {
        if (depositAmount <= 0) {
            addToastMessage("Deposit amount must be greater than 0", "error");
            return;
        }
        if (depositAmount > 100000) {
            addToastMessage("Deposit exceeds maximum limit of R100,000", "error");
            return;
        }
        balance += depositAmount;
        depositAmount = 0;
        addToastMessage("Deposit successful!", "success");
    }


    public void withdraw() {
        if (withdrawAmount <= 0) {
            addToastMessage("Withdrawal amount must be greater than 0", "error");
            return;
        }
        if (withdrawAmount > balance) {
            addToastMessage("Insufficient funds", "error");
            return;
        }
        if (withdrawAmount > 10000) {
            addToastMessage("Withdrawal exceeds daily limit of R10,000", "error");
            return;
        }
        balance -= withdrawAmount;
        withdrawAmount = 0;
        addToastMessage("Withdrawal successful!", "success");
    }

    private void addToastMessage(String message, String type) {
        FacesMessage.Severity severity = type.equals("error")
                ? FacesMessage.SEVERITY_ERROR
                : FacesMessage.SEVERITY_INFO;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, message, null));
    }

    public void logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().invalidateSession();

        try {
            // Redirect to the login page on the auth service with a flag
            context.getExternalContext().redirect("http://localhost:8081/login?logout=true");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
