package be.enyed.example.viewmodel;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.ExecutionParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;

import be.enyed.example.model.BudgetLine;
import be.enyed.example.model.DummyUser;
import be.enyed.example.service.ExampleService;

public class BudgetLinesViewmodel {
	
	private DummyUser user;
	private ExampleService store;
	
	private ListModelList<BudgetLine> lines;
	private BudgetLine line;
	
	private Long cost;
	private String description;
	
	@Init
	public void init(@ExecutionParam("store") ExampleService store, @ExecutionParam("user") DummyUser user){
		this.store = store;
		this.user = user;
		this.lines = new ListModelList<>(store.getAllBudgetLines());
		this.line = lines.get(0);
	}
	
	@Command
	@NotifyChange("line")
	public void addExpenses(){
		line = store.AddExpenses(line, description, cost);
	}
	
	@Command
	public void reload() {
		lines.clear();
		lines.addAll(store.getAllBudgetLines());
		//line ?
		line = lines.stream().filter( l -> l.getId().equals(line.getId())).findFirst().orElse(null);
		setLine(line);
		
		BindUtils.postNotifyChange(null, null, this, ".");
	}
	
	public DummyUser getUser() {
		return user;
	}
	
	public ListModelList<BudgetLine> getLines() {
		return lines;
	}
	
	public void setLine(BudgetLine line) {
		this.line = line;
		this.cost = null;
		this.description = null;
	}
	
	public BudgetLine getLine() {
		return line;
	}
	
	@DependsOn("line")
	public Long getCost() {
		return cost;
	}

	public void setCost(Long cost) {
		this.cost = cost;
	}
	
	@DependsOn("line")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
