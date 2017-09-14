package be.enyed.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BudgetLine {
	
	private UUID id;
	private String name;
	private Long amount;
	private List<Expenses> expenses;
	
	public BudgetLine(String name, Long amount) {
		id = UUID.randomUUID();
		this.name = name;
		this.amount = amount;
		this.expenses = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public List<Expenses> getExpenses() {
		return expenses;
	}

	public void setExpenses(List<Expenses> expenses) {
		this.expenses = expenses;
	}

	public UUID getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BudgetLine other = (BudgetLine) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
	
	public Long getRemainingAmount(){
		return amount - expenses.stream().mapToLong(e -> e.getCost()).sum();
	}

	@Override
	public String toString() {
		return String.format("BudgetLine [name=%s, amount=%s]", name, amount);
	}
	

}
