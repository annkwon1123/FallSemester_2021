import java.text.DecimalFormat;
import java.util.*;

class BankAccount {
	private int balance;
	
	public BankAccount(int initial_amount) {
		if(initial_amount >= 0)
			balance = initial_amount;
		else
			balance = 0;
	}
	
	public int getBalance() {
		return balance;
	}
	
	public boolean deposit(int amount) {
		boolean result = false;
		if (amount < 0)
			System.out.println("invalid deposit amount");
		else {
			balance += amount;
			result = true;
		}
		return result;
	}
	
	public boolean withdraw(int amount) {
		boolean result = false;
		if (amount < 0)
			System.out.println("invalid withdraw amount");
		else if (amount < balance)
			System.out.println("not enough balance");
		else { 
			balance -= amount;
			result = true;
		}
		return result;
	}
}

class BankReader {
	private String input_line = "";
	private Scanner scan;
	
	public BankReader() {
		scan = new Scanner(System.in);
	}
	
	public char readCommand(String message) {
		System.out.print(message);
		input_line = scan.nextLine().toUpperCase();
		System.out.println();
		return input_line.charAt(0);
	}
	
	public int readAmount() {
		int answer = 0;
		String s  = input_line.substring(1, input_line.length());
		
		if(s.length() > 0) {
			double dollars_cents = Double.valueOf(s);
			answer = (int) (dollars_cents * 100);
			// 0.00 형태로 센트단위까지 받은 입력에 *100을 해서 integer형으로 바꾸는 과
		}
		else
			System.out.println("invalid command - input amount: 0");
		return answer;
	}
}

class BankWriter {
	private BankAccount bank;
	private String last_transaction = "";
	
	public BankWriter(BankAccount b) {
		bank = b;
	}
	
	public String unconvert(int i) {
		return new DecimalFormat("0.00").format(i/100.0);
		// 입력받을 때 *100을 해주어 integer형으로 바꾼 것을 다시 double형으로 바꾸는 과
	}
	
	public void setTransaction(String message, int amount) {
		last_transaction = message + " " + unconvert(amount);
		System.out.println("transaction: " + last_transaction);
	}
	
	public void setTransaction(String message) {
		last_transaction = message;
		System.out.println("transaction: " + last_transaction);
	}
}

class AccountController {
	private BankReader reader;
	private BankAccount primary_account, secondary_account, account;
	private BankWriter primary_writer, secondary_writer, writer;
	
	public AccountController(BankReader r, BankAccount a1, BankWriter w1, BankAccount a2, BankWriter w2) {
		reader = r;
		primary_account = a1;
		primary_writer = w1;
		secondary_account = a2;
		secondary_writer = w2;
		account = primary_account;
		writer = primary_writer;
	}
	
	public void resetAccount(BankAccount new_account, BankWriter new_writer) {
		account = new_account;
		writer = new_writer;
	}
	
	public void processTransactions() {
		char command = reader.readCommand("Commands (P/S/D/W/T/I/Q): ");
		switch (command) {
			case 'P':
				resetAccount(primary_account, primary_writer);
				break;
			case 'S':
				resetAccount(secondary_account, secondary_writer);
				break;
			case 'Q':
				System.out.println("Quit");
				return;
			case 'D': {
				int amount = reader.readAmount();
				if (account.deposit(amount))
					writer.setTransaction("deposit $", amount);
				else 
					writer.setTransaction("deposit error");
				break;
			}
			case 'W': {
				int amount = reader.readAmount();
				if (account.withdraw(amount))
					writer.setTransaction("withdraw $", amount);
				else
					writer.setTransaction("withdraw error");
				break;	
			}
			case 'T': {
				int amount = reader.readAmount();
				if (account.withdraw(amount)) {
					if (account == primary_account) 
						secondary_account.deposit(amount);
					else
						primary_account.deposit(amount);
					writer.setTransaction("transfer $", amount);
				}
				else
					writer.setTransaction("transfer error");
				break;
			}
			case 'I':{
				int amount = reader.readAmount();
				if (amount >= 0  && amount <= 100) {
					amount = (int)((amount/100.0) * account.getBalance());
					account.deposit(amount);
					writer.setTransaction("interest $", amount);
				}
				else
					writer.setTransaction("interest error");
				break;
			}
			default:
				writer.setTransaction("invlid command" + command);
		}
		
		System.out.println("=======================");
	    System.out.println("== currently active : " + ((account == primary_account) ? "primary" : "secondary"));
	    System.out.println("== primary account : " + primary_writer.unconvert(primary_account.getBalance()));
	    System.out.println("== secondary account : " + secondary_writer.unconvert(secondary_account.getBalance()));
	    System.out.println("=======================");

	    this.processTransactions();
	}
}

public class H5_Q4 {
	public static void main(String[] args) {
		BankReader reader = new BankReader();
		BankAccount primary_account = new BankAccount(0);
		BankWriter primary_writer = new BankWriter(primary_account);
	    BankAccount secondary_account = new BankAccount(0);
	    BankWriter secondary_writer = new BankWriter(secondary_account);
	    AccountController controller = new AccountController(reader, primary_account, primary_writer, secondary_account,secondary_writer);
	    controller.processTransactions();
	}
}
