package com.dblab;

import com.dblab.model.CourseModel;
import com.dblab.model.MajorModel;
import com.dblab.model.ProfileModel;
import com.dblab.state.StateNewUser;
import com.dblab.util.DBFieldUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DBHelper {

    private static Connection _con = null;

    static void init() {
        checkConnection();
    }

    private static void checkConnection() {
        if (_con == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                _con = DriverManager.getConnection(LocalConfigs.CONNECTION_STRING, LocalConfigs.USERNAME, LocalConfigs.PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(2);
            }
        }
    }

    /**
     * @param userID
     * @throws SQLException if student already exists (duplicate UserID)
     */
    static void createNewStudent(int userID) throws SQLException {
        _con.createStatement().execute("INSERT INTO student (UserID, State, RegistrationDate) VALUES (" + userID + ", \"" + StateNewUser.VALUE + "\", NOW())");
    }

    // student table
    public static final String FIELD_UserID = "UserID";
    public static final String FIELD_State = "State";
    public static final String FIELD_Substate = "Substate";
    public static final String FIELD_RegistrationDate = "RegistrationDate";
    public static final String FIELD_FirstName = "FirstName";
    public static final String FIELD_LastName = "LastName";

    // major table
    public static final String FIELD_MajorID = "MajorID";
    public static final String FIELD_MajorName = "MajorName";
    public static final String FIELD_University = "University";
    public static final String FIELD_CourseID = "CourseID";
    public static final String FIELD_Code = "Code";
    public static final String FIELD_CourseName = "CourseName";
    public static final String FIELD_Category = "Category";
    public static final String FIELD_Credit = "Credit";
    public static final String FIELD_Teacher = "Teacher";
    public static final String FIELD_Day1Start = "Day1Start";
    public static final String FIELD_Day1End = "Day1End";
    public static final String FIELD_Day2Start = "Day2Start";
    public static final String FIELD_Day2End = "Day2End";
    public static final String FIELD_Day3Start = "Day3Start";
    public static final String FIELD_Day3End = "Day3End";
    public static final String FIELD_ExamDate = "ExamDate";
    public static final String FIELD_ExamDurationMinute = "ExamDurationMinute";
    public static final String FIELD_Semester = "Semester";

    // course table
    public static final String FIELD_EntranceYear = "EntranceYear";

    private static boolean isStringType(String field) {
        if (field.equals(FIELD_UserID)) return false;
        else if (field.equals(FIELD_State)) return true;
        else if (field.equals(FIELD_Substate)) return true;
        else if (field.equals(FIELD_RegistrationDate)) return true;
        else if (field.equals(FIELD_FirstName)) return true;
        else if (field.equals(FIELD_LastName)) return true;
        else if (field.equals(FIELD_MajorID)) return false;
        else if (field.equals(FIELD_MajorName)) return true;
        else if (field.equals(FIELD_University)) return true;
        else if (field.equals(FIELD_EntranceYear)) return false;
        else if (field.equals(FIELD_CourseID)) return false;
        else if (field.equals(FIELD_Code)) return false;
        else if (field.equals(FIELD_CourseName)) return true;
        else if (field.equals(FIELD_Category)) return false;
        else if (field.equals(FIELD_Credit)) return false;
        else if (field.equals(FIELD_Teacher)) return true;
        else if (field.equals(FIELD_Day1Start)) return false;
        else if (field.equals(FIELD_Day1End)) return false;
        else if (field.equals(FIELD_Day2Start)) return false;
        else if (field.equals(FIELD_Day2End)) return false;
        else if (field.equals(FIELD_Day3Start)) return false;
        else if (field.equals(FIELD_Day3End)) return false;
        else if (field.equals(FIELD_ExamDate)) return true;
        else if (field.equals(FIELD_ExamDurationMinute)) return false;
        else if (field.equals(FIELD_Semester)) return false;
        else throw new RuntimeException("isStringType(field): Unknown field " + field);
    }

    /**
     * gets specified <code>fields</code> from given <code>table</code>
     * @param table table name
     * @param primaryField the name of the table's primary field
     * @param id value of the primary field
     * @param fields an array of selecting fields
     * @return a map of field names to values
     * @throws SQLException
     */
    public static HashMap<String, Object> getTableFields(String table, String primaryField, Object id, String ... fields) throws SQLException {
        ResultSet rs = _con.createStatement().executeQuery("SELECT " + DBFieldUtil.join("", ", ", "", fields) + " FROM " + table + " WHERE " + primaryField + " = " + id);
        HashMap<String, Object> fieldValues = null;
        while (rs.next()) {
            fieldValues = new HashMap<String, Object>();
            for (String field : fields) {
                if (isStringType(field)) {
                    fieldValues.put(field, rs.getString(field));
                } else {
                    fieldValues.put(field, rs.getInt(field));
                }
            }
            break;
        }
        return fieldValues;
    }

    public static void setStudentState(int userID, String state) throws SQLException {
        setStudentField(userID, FIELD_State, state);
    }

    public static void setStudentSubstate(int userID, String substate) throws SQLException {
        setStudentField(userID, FIELD_Substate, substate);
    }

    /**
     * Sets <code>field</code> in student table by <code>value</code>, for user with <code>userID</code>
     * @param userID
     * @param field The field to be set. Use <code>DBHelper.FIELD_*</code> values.
     * @param value
     * @throws SQLException
     */
    public static void setStudentField(int userID, String field, Object value) throws SQLException {
        if (value == null) {
            _con.createStatement().execute("UPDATE student SET " + field + " = NULL WHERE UserID = " + userID);
            return;
        }
        if (isStringType(field)) {
            _con.createStatement().execute("UPDATE student SET " + field + " = \"" + value + "\" WHERE UserID = " + userID);
        } else {
            _con.createStatement().execute("UPDATE student SET " + field + " = " + value + " WHERE UserID = " + userID);
        }
    }

    public static Object getStudentField(int userID, String field) throws SQLException {
        ResultSet rs = _con.createStatement().executeQuery("SELECT " + field + " FROM student WHERE UserID = " + userID);
        Object result = null;
        while (rs.next()) {
            if (isStringType(field)) {
                result = rs.getString(field);
            }
            else {
                result = rs.getInt(field);
            }
            break;
        }
        return result;
    }

    /**
     * Sets <code>field</code> in major table by <code>value</code>, for user with <code>majorID</code>
     * @param majorID
     * @param field The field to be set. Use <code>DBHelper.FIELD_*</code> values.
     * @param value
     * @throws SQLException
     */
    static void setMajorField(int majorID, String field, Object value) throws SQLException {
        if (isStringType(field)) {
            _con.createStatement().execute("UPDATE major SET " + field + " = \"" + value + "\" WHERE MajorID = " + majorID);
        } else {
            _con.createStatement().execute("UPDATE major SET " + field + " = " + value + " WHERE MajorID = " + majorID);
        }
    }

    public static Object getMajorFieldForUser(int userID, String field) throws SQLException {
        ResultSet rs = _con.createStatement().executeQuery("SELECT " + field + " FROM major WHERE MajorID = (SELECT MajorID FROM student WHERE UserID = " + userID + " LIMIT 1)");
        Object result = null;
        while (rs.next()) {
            if (isStringType(field)) {
                result = rs.getString(field);
            }
            else {
                result = rs.getInt(field);
            }
            break;
        }
        return result;
    }

    public static Object getMajorField(int majorID, String field) throws SQLException {
        ResultSet rs = _con.createStatement().executeQuery("SELECT " + field + " FROM major WHERE MajorID = " + majorID);
        Object result = null;
        while (rs.next()) {
            if (isStringType(field)) {
                result = rs.getString(field);
            }
            else {
                result = rs.getInt(field);
            }
            break;
        }
        return result;
    }

    static String getStudentState(int userID) throws SQLException {
        ResultSet result = _con.createStatement().executeQuery("SELECT State FROM student WHERE UserID = " + userID);
        String state = null;
        while (result.next()) {
            state = result.getString(FIELD_State);
            break;
        }
        return state;
    }

    public static String getStudentSubstate(int userID) throws SQLException {
        ResultSet result = _con.createStatement().executeQuery("SELECT Substate FROM student WHERE UserID = " + userID);
        String substate = null;
        while (result.next()) {
            substate = result.getString(FIELD_Substate);
            break;
        }
        return substate;
    }

    public static void setStudentMajorByFields(int userID, MajorModel majorModel) throws SQLException {
        _con.createStatement().execute("CALL SET_STUDENT_MAJOR_BY_FIELDS ("
                + userID + ", \"" + majorModel.majorName + "\", \"" + majorModel.university + "\", " + majorModel.entranceYear + ")");
    }

    public static void addStudentCourseByFields(int userID, CourseModel courseModel) throws SQLException {
        _con.createStatement().execute("CALL ADD_STUDENT_COURSE_BY_FIELDS ("
                + userID
                + ", " + courseModel.code
                + ", \"" + courseModel.courseName + "\""
                + ", " + courseModel.category
                + ", " + courseModel.credit
                + ", \"" + courseModel.teacher + "\""
                + ", " + courseModel.day1Start
                + ", " + courseModel.day1End
                + ", " + courseModel.day2Start
                + ", " + courseModel.day2End
                + ", " + courseModel.day3Start
                + ", " + courseModel.day3End
                + ", " + courseModel.examDate
                + ", " + courseModel.examDurationMinute
                + ", " + courseModel.semester
                + ")"
        );
    }

    static ProfileModel getStudentProfile(int userID) throws SQLException {
        ResultSet result = _con.createStatement().executeQuery("SELECT "
                + FIELD_FirstName + ", "
                + FIELD_LastName + ", "
                + FIELD_MajorName + ", "
                + FIELD_University + ", "
                + FIELD_EntranceYear
                + " FROM (student LEFT JOIN major ON student.MajorID = major.MajorID) WHERE UserID = " + userID);
        ProfileModel profile = null;
        while (result.next()) {
            profile = new ProfileModel();
            profile.firstName = result.getString(FIELD_FirstName);
            profile.lastName = result.getString(FIELD_LastName);
            profile.majorName = result.getString(FIELD_MajorName);
            profile.university = result.getString(FIELD_University);
            profile.entranceYear = result.getInt(FIELD_EntranceYear);
            break;
        }
        return profile;
    }
}
