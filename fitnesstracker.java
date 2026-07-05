import java.util.*;
import java.io.*;

public class fitnesstracker {

    static final String USER_DATA_FILE = "data.txt";
    static final String USER_REPORT_FILE = "user_fitness_report.txt";
    static final String HISTORY_FILE = "user_history.txt";
    static final int WATER_GOAL = 8;
    static String user_name = "";
    static String password = "";
    static int age = 0;
    static char gender = 'U';
    static double weight = 0;
    static double heightCm = 0;
    static String goalType = "Maintain";
    static double targetWeight = 0;
    static boolean loggedIn = false;
    static boolean weeklyDataEntered = false;
    static int waterGlasses = 0;
    static ArrayList<String> meal_Names = new ArrayList<>();
    static ArrayList<Integer> meal_Calories = new ArrayList<>();
    static ArrayList<String> progress_History = new ArrayList<>();
    static ArrayList<String> memberNames = new ArrayList<>();
    static ArrayList<String> memberPasswords = new ArrayList<>();
    static double[] weekly_Weights = new double[7];
    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        showBanner();
        loadFromFile();
        loadHistory();
        int mychoice;
        do {
            System.out.println("\n1: Register");
            System.out.println("2: Login");
            System.out.println("3: Exit");
            System.out.print("Enter your choice: ");
            try {
                mychoice = input.nextInt();
                 input.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number.");
                mychoice = -1;
            }
            switch (mychoice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    boolean ok = login();
                    if (ok) mainMenu();
                    break;
                case 3:
                    System.out.println("Goodbye! Stay fit, stay healthy!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (mychoice != 3);
    }

    static void showBanner() {
        System.out.println("=============================================================");
        System.out.println("||      FITNESS & NUTRITIONAL GOAL TRACKER                ||");
        System.out.println("=============================================================");
    }

    static void registerUser() {
        
        System.out.println("\n--- REGISTER ---");

        System.out.print("Enter username: ");
        String username = input.nextLine();

        // check duplicate
        for (int i = 0; i < memberNames.size(); i++) {
            if (memberNames.get(i).equals(username)) {
                System.out.println("Username already exists! Try another.");
                return;
            }
        }

        System.out.print("Enter password: ");
        String pass = input.nextLine();

        // yahan user ka data file mein save kar raha hoon
        while (true) {
    try {
        System.out.print("Enter age: ");
        age = Integer.parseInt(input.nextLine());
        if (age > 0) break;
        System.out.println("Age must be positive!");
    } catch (NumberFormatException e) {
        System.out.println("Invalid! Enter a number!");
    }
}
        // System.out.print("Enter age: ");
        // age = Integer.parseInt(input.nextLine());

        while (true) {
            System.out.print("Enter gender (M/F/O): ");
            String g = input.nextLine();
            if (g.length() > 0) {
                gender = Character.toUpperCase(g.charAt(0));
                if (gender == 'M' || gender == 'F' || gender == 'O') break;
            }
            System.out.println("Please enter M, F, or O.");
        }

        System.out.print("Enter current weight in kg: ");
        weight = Double.parseDouble(input.nextLine());

        System.out.print("Enter height in cm: ");
        heightCm = Double.parseDouble(input.nextLine());

        System.out.println("Choose your goal:");
        System.out.println("1: Bulk");
        System.out.println("2: Cut");
        System.out.println("3: Maintain");
        System.out.print("Enter choice: ");
        int goalChoice = Integer.parseInt(input.nextLine());
        if (goalChoice == 1)      goalType = "Bulk";
        else if (goalChoice == 2) goalType = "Cut";
        else                      goalType = "Maintain";

        System.out.print("Enter target weight in kg: ");
        targetWeight = Double.parseDouble(input.nextLine());

        // save to lists
        memberNames.add(username);
        memberPasswords.add(pass);
        user_name = username;
        password = pass;

        // save to file
        try {
            FileWriter fw = new FileWriter(USER_DATA_FILE, true);
            fw.write(username + "," + pass + "," + age + "," + gender + ","
                   + weight + "," + heightCm + "," + goalType + "," + targetWeight + "\n");
            fw.close();
            System.out.println("Registration successful! Welcome, " + username);
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }

        loggedIn = true;
        meal_Names.clear();
        meal_Calories.clear();
        waterGlasses = 0;
        showProfile();
    }

    static boolean login() {
        System.out.print("Enter username: ");
        String username = input.nextLine();
        System.out.print("Enter password: ");
        String pass = input.nextLine();

        for (int i = 0; i < memberNames.size(); i++) {
            if (memberNames.get(i).equals(username) &&
                memberPasswords.get(i).equals(pass)) {
                loggedIn = true;
                user_name = username;
                password = pass;

                // load this user's data from file
                loadUserData(username);

                System.out.println("Login successful! Welcome, " + username);
                return true;
            }
        }
        System.out.println("Wrong username or password!");
        return false;
    }

    static void loadFromFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(USER_DATA_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    memberNames.add(parts[0]);
                    memberPasswords.add(parts[1]);
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            
        } catch (IOException e) {
            System.out.println("Error loadiing data: " + e.getMessage());
        }
    }
    
    static void loadUserData(String username) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(USER_DATA_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8 && parts[0].equals(username)) {
                    age        = Integer.parseInt(parts[2]);
                    gender     = parts[3].charAt(0);
                    weight     = Double.parseDouble(parts[4]);
                    heightCm   = Double.parseDouble(parts[5]);
                    goalType   = parts[6];
                    targetWeight = Double.parseDouble(parts[7]);
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error loading user data: " + e.getMessage());
        }
    }

    static void loadHistory() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(HISTORY_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                progress_History.add(line);
            }
            br.close();
            if (!progress_History.isEmpty()) {
                System.out.println("History loaded: " + progress_History.size() + " entries.");
            }
        } catch (FileNotFoundException e) {
            
        } catch (IOException e) {
            System.out.println("Error loading history: " + e.getMessage());
        }
    }

    static void mainMenu() {
        int choice;
        do {
            System.out.println("\n=============================================================");
            System.out.println("Logged in as: " + user_name + " | Goal: " + goalType
                             + " | Water: " + waterGlasses + "/" + WATER_GOAL);
            System.out.println("=============================================================");
            System.out.println("1. Calculate BMI");
            System.out.println("2. Add Meal");
            System.out.println("3. Show Calories");
            System.out.println("4. Water Tracker");
            System.out.println("5. Weekly Report");
            System.out.println("6. Reset Daily Data");
            System.out.println("7. Show History");
            System.out.println("8. Save Report");
            System.out.println("0. Logout");
            System.out.println("=============================================================");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
                choice = -1;
            }

            switch (choice) {
                case 1: calculateBMI();   break;
                case 2: addMeal();        break;
                case 3: showCalories();   break;
                case 4: waterTracker();   break;
                case 5: weeklyReport();   break;
                case 6: resetDailyData(); break;
                case 7: showHistory();    break;
                case 8: saveReport();     break;
                case 0:
                    System.out.println("Logged out successfully!");
                    loggedIn = false;
                    user_name = "";
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 0);
    }

    static void showProfile() {
        System.out.println("\n--- USER PROFILE ---");
        System.out.println("Username  : " + user_name);
        System.out.println("Age       : " + age);
        System.out.println("Gender    : " + gender);
        System.out.println("Weight    : " + weight + " kg");
        System.out.println("Height    : " + heightCm + " cm");
        System.out.println("Goal      : " + goalType);
        System.out.println("Target Wt : " + targetWeight + " kg");
    }

    static void calculateBMI() {
        if (!loggedIn) { System.out.println("Please login first."); return; }

        double heightMeters = heightCm / 100.0;
        double bmi = Math.round((weight / (heightMeters * heightMeters)) * 100.0) / 100.0;

        System.out.println("\n--- BMI CALCULATOR ---");
        System.out.printf("Weight : %.2f kg%n", weight);
        System.out.printf("Height : %.2f cm%n", heightCm);
        System.out.printf("BMI    : %.2f%n", bmi);

        if (bmi < 18.5)     
            System.out.println("Status: Underweight - focus on healthy meals.");
        else if (bmi < 25) 
             System.out.println("Status: Normal - keep balancing food and workouts.");
        else if (bmi < 30) 
             System.out.println("Status: Overweight - light calorie control may help.");
        else               
             System.out.println("Status: Obese - consult a health expert.");
    }

    static void addMeal() {
        if (!loggedIn) { System.out.println("Please login first."); return; }

        System.out.println("\n--- ADD MEAL ---");
        System.out.print("How many meals to add? ");
        int count = Integer.parseInt(input.nextLine());

        for (int i = 0; i < count; i++) {
            System.out.print("Meal name: ");
            String name = input.nextLine();
            System.out.print("Calories: ");
            int cal = Integer.parseInt(input.nextLine());
            meal_Names.add(name);
            meal_Calories.add(cal);
        }
        System.out.println("Meals added successfully!");
    }

    static void showCalories() {
        if (!loggedIn) { System.out.println("Please login first."); return; }
        if (meal_Names.isEmpty()) { System.out.println("No meals added yet."); return; }

        System.out.println("\n--- CALORIES SUMMARY ---");
        int total = 0;
        for (int i = 0; i < meal_Names.size(); i++) {
            System.out.println((i+1) + ". " + meal_Names.get(i)
                             + " - " + meal_Calories.get(i) + " cal");
            total += meal_Calories.get(i);
        }

        double suggested = weight * 30;
        if (goalType.equals("Bulk"))      suggested += 300;
        else if (goalType.equals("Cut"))  suggested -= 300;
        if (suggested < 1200)             suggested = 1200;

        System.out.println("---------------------------");
        System.out.println("Total Calories   : " + total);
        System.out.printf("Suggested Intake : %.0f%n", suggested);
        if (total < suggested)      System.out.println("You are below your target.");
        else if (total > suggested) System.out.println("You are above your target.");
        else                        System.out.println("Perfect match!");
    }

    static void waterTracker() {
        if (!loggedIn) { System.out.println("Please login first."); return; }

        System.out.println("\n--- WATER TRACKER ---");
        System.out.print("Glasses of water today: ");
        waterGlasses = Integer.parseInt(input.nextLine());

        System.out.println("Goal  : " + WATER_GOAL + " glasses");
        System.out.println("Today : " + waterGlasses + " glasses");
        if (waterGlasses >= WATER_GOAL)
            System.out.println("Great job! Goal achieved!");
        else
            System.out.println("Need " + (WATER_GOAL - waterGlasses) + " more glass(es).");
    }

    static void weeklyReport() {
        if (!loggedIn) { System.out.println("Please login first."); return; }

        System.out.println("\n--- WEEKLY REPORT ---");
        for (int i = 0; i < 7; i++) {
            System.out.print("Day " + (i+1) + " weight (kg): ");
            weekly_Weights[i] = Double.parseDouble(input.nextLine());
        }

        double total = 0, highest = weekly_Weights[0], lowest = weekly_Weights[0];
        for (int i = 0; i < 7; i++) {
            total += weekly_Weights[i];
            if (weekly_Weights[i] > highest) highest = weekly_Weights[i];
            if (weekly_Weights[i] < lowest)  lowest  = weekly_Weights[i];
        }
        double average = total / 7;

        System.out.printf("%-8s %-12s %-10s%n", "Day", "Weight(kg)", "Change");
        System.out.println("--------------------------------");
        for (int i = 0; i < 7; i++) {
            double change = (i == 0) ? 0 : weekly_Weights[i] - weekly_Weights[i-1];
            System.out.printf("%-8d %-12.2f %-10.2f%n", i+1, weekly_Weights[i], change);
        }
        System.out.printf("Average : %.2f kg%n", average);
        System.out.printf("Highest : %.2f kg%n", highest);
        System.out.printf("Lowest  : %.2f kg%n", lowest);

        if (Math.abs(weekly_Weights[6] - targetWeight) < Math.abs(weekly_Weights[0] - targetWeight))
            System.out.println("Good progress! Moving closer to target.");
        else if (Math.abs(weekly_Weights[6] - targetWeight) > Math.abs(weekly_Weights[0] - targetWeight))
            System.out.println("Moved away from target this week. Keep trying!");
        else
            System.out.println("Same distance from target. Push harder next week!");

        weight = weekly_Weights[6];
        weeklyDataEntered = true;
    }

    static void resetDailyData() {
        meal_Names.clear();
        meal_Calories.clear();
        waterGlasses = 0;
        System.out.println("Daily data reset successfully!");
    }

    static void showHistory() {
        System.out.println("\n--- YOUR PROGRESS HISTORY ---");
        if (progress_History.isEmpty()) {
            System.out.println("No history yet. Save a report first.");
            return;
        }
        for (int i = 0; i < progress_History.size(); i++) {
            System.out.println((i+1) + ". " + progress_History.get(i));
        }
    }

    static void saveReport() {
        if (!loggedIn) { System.out.println("Please login first."); return; }

        // calculate BMI
        double heightMeters = heightCm / 100.0;
        double bmi = Math.round((weight / (heightMeters * heightMeters)) * 100.0) / 100.0;
        String bmiStatus;
        if (bmi < 18.5)     bmiStatus = "Underweight";
        else if (bmi < 25)  bmiStatus = "Normal";
        else if (bmi < 30)  bmiStatus = "Overweight";
        else                bmiStatus = "Obese";

        // calculate total calories
        int totalCalories = 0;
        for (int i = 0; i < meal_Calories.size(); i++) {
            totalCalories += meal_Calories.get(i);
        }

        // calculate suggested calories
        double suggested = weight * 30;
        if (goalType.equals("Bulk"))     suggested += 300;
        else if (goalType.equals("Cut")) suggested -= 300;
        if (suggested < 1200)            suggested = 1200;

        try {
            // REPORT FILE - overwrites each time
            FileWriter fw = new FileWriter(USER_REPORT_FILE);
            fw.write("==============================================\n");
            fw.write("     FITNESS & NUTRITION GOAL TRACKER        \n");
            fw.write("==============================================\n");
            fw.write("Report Date : " + new java.util.Date() + "\n\n");

            fw.write("--- USER DETAILS ---\n");
            fw.write("Username  : " + user_name + "\n");
            fw.write("Age       : " + age + "\n");
            fw.write("Gender    : " + gender + "\n");
            fw.write("Weight    : " + weight + " kg\n");
            fw.write("Height    : " + heightCm + " cm\n");
            fw.write("Goal      : " + goalType + "\n");
            fw.write("Target Wt : " + targetWeight + " kg\n\n");

            fw.write("--- BMI DETAILS ---\n");
            fw.write("BMI Value  : " + bmi + "\n");
            fw.write("BMI Status : " + bmiStatus + "\n\n");

            fw.write("--- MEAL DETAILS ---\n");
            if (meal_Names.isEmpty()) {
                fw.write("No meals added today.\n");
            } else {
                for (int i = 0; i < meal_Names.size(); i++) {
                    fw.write((i+1) + ". " + meal_Names.get(i)
                           + " - " + meal_Calories.get(i) + " cal\n");
                }
            }
            fw.write("Total Calories    : " + totalCalories + "\n");
            fw.write("Suggested Calories: " + (int)suggested + "\n");
            if (totalCalories < suggested)      fw.write("Status: Below target\n\n");
            else if (totalCalories > suggested) fw.write("Status: Above target\n\n");
            else                                fw.write("Status: Perfect match\n\n");

            fw.write("--- WATER TRACKER ---\n");
            fw.write("Water Today : " + waterGlasses + "/" + WATER_GOAL + " glasses\n");
            if (waterGlasses >= WATER_GOAL) fw.write("Status: Goal achieved!\n\n");
            else fw.write("Status: Need " + (WATER_GOAL - waterGlasses) + " more glass(es)\n\n");

            fw.write("--- WEEKLY WEIGHTS ---\n");
            if (weeklyDataEntered) {
                double wtotal = 0, high = weekly_Weights[0], low = weekly_Weights[0];
                for (int i = 0; i < 7; i++) {
                    fw.write("Day " + (i+1) + " : " + weekly_Weights[i] + " kg\n");
                    wtotal += weekly_Weights[i];
                    if (weekly_Weights[i] > high) high = weekly_Weights[i];
                    if (weekly_Weights[i] < low)  low  = weekly_Weights[i];
                }
                fw.write("Average : " + (wtotal/7) + " kg\n");
                fw.write("Highest : " + high + " kg\n");
                fw.write("Lowest  : " + low  + " kg\n\n");
            } else {
                fw.write("No weekly data entered yet.\n\n");
            }
            fw.write("Consistency matters more than perfection. Keep going!\n");
            fw.close();
            System.out.println("Report saved to " + USER_REPORT_FILE);

            // HISTORY FILE - appends each time
            FileWriter hw = new FileWriter(HISTORY_FILE, true);
            hw.write("User=" + user_name
                   + " | Goal=" + goalType
                   + " | BMI=" + bmi
                   + " | Calories=" + totalCalories
                   + " | Water=" + waterGlasses + "/" + WATER_GOAL
                   + " | Date=" + new java.util.Date() + "\n");
            hw.close();
            System.out.println("History updated in " + HISTORY_FILE);

            
            progress_History.add("User=" + user_name + " | Goal=" + goalType + " | BMI=" + bmi + " | Cal=" + totalCalories + " | Water=" + waterGlasses + "/" + WATER_GOAL);

        } catch (IOException e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
    }
    static void bulk_workout(){
        

    }
}
