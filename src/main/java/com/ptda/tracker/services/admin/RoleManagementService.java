package com.ptda.tracker.services.admin;

import com.ptda.tracker.models.admin.Admin;
import com.ptda.tracker.models.assistance.Assistant;
import com.ptda.tracker.models.user.User;

public interface RoleManagementService {

    Assistant promoteUserToAssistant(User user);

    Admin promoteUserToAdmin(User user);

    User demoteAssistant(Assistant assistant);

    Admin promoteAssistant(User user);

    User demoteAdminToUser(Admin admin);

    Assistant demoteAdminToAssistant(Admin admin);

}
