package org.example.bean;

import lombok.Getter;
import lombok.Setter;
import org.example.model.User;
import org.example.service.UserClient;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash; // Required for message survival

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Named("adminBean")
@SessionScoped
public class AdminBean implements Serializable {

    private List<User> users = new ArrayList<>();
    private UserClient adminClient = new UserClient();

    private User selectedUser = new User();
    private boolean editMode = false;

    @PostConstruct
    public void init() {
        loadUsers();
    }

    public void loadUsers() {
        users = adminClient.getAllUsers();
    }

    public void deleteUser(Long id) {
        try {
            adminClient.deleteUser(id);
            loadUsers();
            addToastMessage("User deleted successfully!", false);
        } catch (Exception e) {
            addToastMessage("Failed to delete user", true);
        }
    }

    public String editUser(User user) {
        this.selectedUser = new User();
        selectedUser.setId(user.getId());
        selectedUser.setUsername(user.getUsername());
        selectedUser.setEmail(user.getEmail());
        selectedUser.setRole(user.getRole());

        this.editMode = true;
        return "editUser?faces-redirect=true";
    }

    public String saveUser() {
        try {
            adminClient.updateUser(selectedUser);
            loadUsers();
            editMode = false;

            // Keep messages persistent through the oncoming redirect
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            addToastMessage("User updated successfully!", false);

            return "admin?faces-redirect=true";
        } catch (Exception e) {

            addToastMessage("Failed to update user", true);
            return null;
        }
    }

    public String cancelEdit() {
        editMode = false;
        return "admin?faces-redirect=true";
    }

    private void addToastMessage(String message, boolean error) {
        FacesMessage.Severity severity = error ? FacesMessage.SEVERITY_ERROR : FacesMessage.SEVERITY_INFO;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, null));
    }
}
