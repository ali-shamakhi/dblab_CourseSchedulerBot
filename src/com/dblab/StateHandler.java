package com.dblab;

import com.dblab.model.CourseModel;
import com.dblab.model.MajorModel;
import com.dblab.model.UserMap;
import com.dblab.state.*;
import com.pengrad.telegrambot.model.Message;

import java.sql.SQLException;

class StateHandler {

    static UserMap<MajorModel> userMajorMap;
    static UserMap<CourseModel> userCourseMap;

    static {
        userMajorMap = new UserMap<MajorModel>();
        userCourseMap = new UserMap<CourseModel>();
    }

    static void handleMessage(Message incomingMessage) throws SQLException {
        String state = DBHelper.getStudentState(incomingMessage.from().id());
        if (state == null) {
            DBHelper.createNewStudent(incomingMessage.from().id());
            StateNewUser.validate(incomingMessage);
        } else {
            if (state.equals(StateMainScreen.VALUE)) {
                StateMainScreen.validate(incomingMessage);
            }
            else if (state.equals(StateFunctionList.VALUE)) {
                StateFunctionList.validate(incomingMessage);
            }
            else if (state.equals(StateNotRegistered.VALUE)) {
                StateNotRegistered.validate(incomingMessage);
            }
            else if (state.equals(StateRegistrationGetFirstName.VALUE)) {
                StateRegistrationGetFirstName.validate(incomingMessage);
            }
            else if (state.equals(StateRegistrationGetLastName.VALUE)) {
                StateRegistrationGetLastName.validate(incomingMessage);
            }
            else if (state.equals(StateRegistrationGetMajor.VALUE)) {
                StateRegistrationGetMajor.validate(incomingMessage, userMajorMap);
            }
            else if (state.equals(StateCreatingNewCourse.VALUE)) {
                StateCreatingNewCourse.validate(incomingMessage, userCourseMap);
            }
            else {
                System.err.println("Unknown state " + state + " for chat_id " + incomingMessage.chat().id());
            }
        }
    }

}
