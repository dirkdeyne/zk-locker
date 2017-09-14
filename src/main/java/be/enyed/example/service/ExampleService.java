package be.enyed.example.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import be.enyed.example.model.BudgetLine;
import be.enyed.example.model.DummyUser;
import be.enyed.example.model.Expenses;
@Service
public class ExampleService {
	
	private Map<UUID, BudgetLine> budgetLines = new HashMap<>();
	private Set<DummyUser> users = new HashSet<>();
	private List<DummyUser> randomUsers = new ArrayList<>();
	
	public BudgetLine AddBudgetLine(BudgetLine line){
		return budgetLines.put(line.getId(), line);
	}
	
	public BudgetLine AddExpenses(BudgetLine line, String description, Long cost) {
		BudgetLine budgetLine = budgetLines.get(line.getId());
		if(budgetLine.getRemainingAmount() - cost < 0) {
			throw new RuntimeException("Insufficient budget");
		}
		Expenses expense = new Expenses(budgetLine, description, cost);
		budgetLine.getExpenses().add(expense);
		return budgetLine;
	}
	
	public DummyUser getCurrentUser(){
		if(randomUsers.isEmpty()) {
			randomUsers.addAll(users);
		}
		int randomSelection = (int) (randomUsers.size() * Math.random());
		DummyUser random = randomUsers.get(randomSelection);
		randomUsers.remove(randomSelection);
		return random;
	}
	
	public void add(DummyUser user) {
		users.add(user);
	}
	
	public Set<DummyUser> getUsers() {
		return users;
	}

	public Collection<BudgetLine> getAllBudgetLines() {
		return budgetLines.values();
	}
}
