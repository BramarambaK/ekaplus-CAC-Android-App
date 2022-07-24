package com.eka.cacapp.data.insight;

public class InsightFilterHelper {

    public String getInputType(int columType){
        String returnType = "";
        if(columType == -1 || columType==2||columType ==5){
            returnType= "Number";
        }else if (columType==1){
            returnType= "String";
        }else if (columType ==3){
            returnType= "Date";
        }
        return returnType;
    }

    public String[] getOperatorDisplayValues(int columType){
        String arr[] = new String[0];
        if(columType == -1 || columType==2||columType ==5){
            arr = new String[]{"Select Operator","Less than", "Greater than or equal",
                    "Equals", "Not equal", "Is blank", "Is not blank", "Top", "Bottom"};
        }else if (columType==1){
             arr = new String[]{"Select Operator","Contains", "Does not contain",
                    "Starts with", "Does not start with", "Equals", "Not equal", "Is blank", "Is not blank","in"};
        }else if (columType ==3){
            arr = new String[]{"Select Operator","On", "Not on",
                    "On or After", "Before", "Is blank", "Is not blank"};
        }
        return arr;

    }

    public String[] getOperatorActualValues(int columType){
        String arr[] = new String[0];
        if(columType == -1 || columType==2||columType ==5){
            arr = new String[]{"Select Operator","lessThan", "greaterThanOrEqual",
                    "equal", "notEqual", "isBlank", "isNotBlank", "top", "bottom"};
        }else if (columType==1){
            arr = new String[]{"Select Operator","contains", "notContains",
                    "startsWith", "notStartsWith", "equal", "isBlank", "isNotBlank", "in","notEqual"};
        }else if (columType ==3){
            arr = new String[]{"Select Operator","on", "notOn",
                    "onOrAfter", "before", "isBlank", "isNotBlank"};
        }
        return arr;

    }


    public String getOperatorSelectedValue(int columType,int selctedPos){
        String result = getOperatorActualValues(columType)[selctedPos];
        return result;

    }


}
