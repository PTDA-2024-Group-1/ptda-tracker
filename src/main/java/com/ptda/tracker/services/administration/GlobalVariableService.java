package com.ptda.tracker.services.administration;

import com.ptda.tracker.models.admin.GlobalVariableName;

import java.util.Optional;

public interface GlobalVariableService {

    String get(String keyName);

    void set(GlobalVariableName name, String value);

    void delete(String keyName);

}
