import java.util.*;

class UserClass {
    String name;
    int totalTokens;
    int[] tokenNumbers;
    int min, max = 0;
    int participate = 0;
    char voteOption; // To track the vote for X or Y
    boolean votedForWinner = false; // To track if user voted for the winning option
    boolean hasVoted = false; // To track if the user has already voted

    // Add the name and total tokens of the user
    void addName(Scanner cp) {
        System.out.println("Enter your name: ");
        name = cp.next();
        System.out.println("Enter your total tokens: ");
        totalTokens = cp.nextInt();
        tokenNumbers = new int[totalTokens]; // Initialize tokenNumbers array after setting totalTokens
    }

    // Assign token numbers to the user
    void addTokenNumber(int a) {
        min = 10000 * a;
        for (int i = 0; i < totalTokens; i++) {
            tokenNumbers[i] = min + i;
        }
        max = min + totalTokens - 1; // Ensure max is set to the highest token number
    }

    // Increment participation count
    void updateParticipation() {
        participate++;
    }

    // Debit tokens if the user loses
    void debitTokens(int amount) {
        if (totalTokens >= amount) {
            totalTokens -= amount;
        } else {
            totalTokens = 0; // Ensure total tokens don't go negative
        }
    }

    // Credit tokens to the user (for winners)
    void creditTokens(int amount) {
        totalTokens += amount;
    }

    // Get a random token from the user's tokens
    int getRandomToken(Random random) {
        return tokenNumbers[random.nextInt(totalTokens)]; // Select a random token from available tokens
    }
}

public class TokenSystem {
    static List<UserClass> userClassObj = new ArrayList<>(); // Use a List to store user objects
    static Scanner sc = new Scanner(System.in); // Single Scanner instance
    static Random random = new Random(); // Random instance for token selection

    // Add people 
    static void AddThePeople() {
        System.out.println("Enter how many people you want to add: ");
        int newPeopleCount = sc.nextInt();

        for (int i = 0; i < newPeopleCount; i++) {
            UserClass newUser = new UserClass(); // Initialize new UserClass object
            newUser.addName(sc); // Pass the scanner instance
            newUser.addTokenNumber(userClassObj.size() + i + 1); // Assign token numbers based on current size
            userClassObj.add(newUser); // Add the new user to the list
        }
    }

    // Check eligible
    static List<UserClass> eligible() {
        List<UserClass> eligibleVoters = new ArrayList<>();
        for (UserClass user : userClassObj) {
            if (user.totalTokens >= 1000) {
                eligibleVoters.add(user);
            } else {
                System.out.println(user.name + " does not have enough tokens (1000 required) to vote.");
            }
        }

        System.out.println("Total eligible voters: " + eligibleVoters.size());
        return eligibleVoters; // Return eligible voters for further processing
    }

    // Start the game
    static void StartTheVote() {
        List<UserClass> eligibleVoters = eligible(); // Get eligible voters

        // Step 2: Start the voting process only for eligible voters
        if (eligibleVoters.isEmpty()) {
            System.out.println("No one is eligible to vote.");
            return; // End the program if no one is eligible
        }

        int sumX = 0;
        int sumY = 0;
        List<UserClass> votedX = new ArrayList<>();
        List<UserClass> votedY = new ArrayList<>();

        // Voting process for eligible voters
        for (UserClass currentUser : eligibleVoters) {
            if (currentUser.hasVoted) {
                System.out.println(currentUser.name + " has already voted.");
                continue; // Skip if user has already voted
            }

            System.out.println("Enter " + currentUser.name + "'s vote (X/Y)");
            char vote = sc.next().toUpperCase().charAt(0);

            // Input validation for votes
            if (vote != 'X' && vote != 'Y') {
                System.out.println("Invalid vote. Please enter 'X' or 'Y'.");
                continue;
            }

            // Select a random token from the current user
            int selectedToken = currentUser.getRandomToken(random);
            System.out.println(currentUser.name + " has voted using token number: " + selectedToken);

            currentUser.voteOption = vote;
            currentUser.updateParticipation(); // Update participation count
            currentUser.hasVoted = true; // Mark as voted

            if (vote == 'X') {
                votedX.add(currentUser);
                sumX++;
            } else {
                votedY.add(currentUser);
                sumY++;
            }
        }

        // Determine the winning option
        char winningOption;
        List<UserClass> winners = new ArrayList<>();

        if (sumX > sumY) {
            winningOption = 'X';
            winners = votedX;
        } else if (sumX < sumY) {
            winningOption = 'Y';
            winners = votedY;
        } else {
            System.out.println("Vote is a draw.");
            return; // End if there's a draw
        }

        // Mark winners
        for (UserClass user : winners) {
            user.votedForWinner = true;
        }

        // Announce the winning option
        System.out.println("...........................................................................");
        System.out.println("The winning option is: " + winningOption);
        System.out.println("...........................................................................");

        // Deduct tokens from the users who lost (those who did not vote for the winning option)
        int deductionAmount = 500; // The amount of tokens to deduct for losers
        int totalDebitedTokens = 0; // Track total tokens debited from losers

        for (UserClass currentUser : eligibleVoters) {
            if (!currentUser.votedForWinner) {
                currentUser.debitTokens(deductionAmount);
                totalDebitedTokens += deductionAmount;
                System.out.println(currentUser.name + " lost and has been debited " + deductionAmount + " tokens.");
            }

            // Display updated total tokens for each user
            System.out.println("...........................................................................");
            System.out.println("Name: " + currentUser.name);
            System.out.println("Total tokens: " + currentUser.totalTokens);
            System.out.println("...........................................................................");
        }

        // Calculate total tokens of winners
        int totalWinnerTokens = 0;
        for (UserClass winner : winners) {
            totalWinnerTokens += winner.totalTokens;
        }

        // Distribute debited tokens proportionally based on investment
        for (UserClass winner : winners) {
            // Calculate the proportion of the debited tokens this winner should receive
            int tokensToCredit = (int) (((double) winner.totalTokens / totalWinnerTokens) * totalDebitedTokens);
            winner.creditTokens(tokensToCredit);
            System.out.println(winner.name + " has been credited with " + tokensToCredit + " tokens.");
            System.out.println("Updated total tokens: " + winner.totalTokens);
        }
    }

    public static void main(String[] args) {
        char Key;
        do {
            System.out.println("---Menu---"); // Define the menu
            System.out.println("Enter 'A' for add people:");
            System.out.println("Enter 'S' for start the code: ");
            System.out.println("Enter 'X' for Check Eligibility");
            System.out.println("Enter 'E' for Exit the program: ");
            Key = sc.next().charAt(0);
            Key = Character.toUpperCase(Key); // Change to uppercase for easier comparison

            switch (Key) {
                case 'A': // Add people
                    AddThePeople();
                    break;
                case 'S': // Start game
                    StartTheVote();
                    break;
                case 'X': // Check
                    eligible();
                    break;
                case 'E': // Exit
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (true); // For continue the process
    }
}
