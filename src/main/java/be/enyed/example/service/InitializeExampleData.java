package be.enyed.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import be.enyed.example.model.BudgetLine;
import be.enyed.example.model.DummyUser;

@Component
public class InitializeExampleData implements CommandLineRunner {
	
	private final ExampleService store;
	
	@Autowired
	public InitializeExampleData(ExampleService store) {
		this.store = store;
	}


	@Override
	public void run(String... args) throws Exception {
		DummyUser filip = new DummyUser("Filip");
		DummyUser dirk = new DummyUser("Dirk");
		
		store.add(dirk);
		store.add(filip);
		
		
		BudgetLine education = new BudgetLine("Education", 100000l);
		BudgetLine tools = new BudgetLine("Tools", 25000l);
		BudgetLine various = new BudgetLine("Various", 10000l);
		
		store.AddBudgetLine(education);
		store.AddBudgetLine(tools);
		store.AddBudgetLine(various);
		
		store.AddExpenses(education, "Devoxx", 6260l);
		store.AddExpenses(tools, "ZK Professional", 899l);
		store.AddExpenses(various, "Social event", 320l);
		
		/*
		Message world = new Message("Hello World!");
		Message zk = new Message("Hello ZK!");		
		Message locker = new Message("Hello Locker!");
		
		world.addComent(dirk, "Hi there!");
		world.addComent(filip, "Hallo");
		
		zk.addComent(filip, "Need some help?");
		zk.addComent(filip, "Just ask me.");
		zk.addComent(dirk, "Thanks, I really could use some help.");
		
		locker.addComent(dirk, "Just made a locking component.");
		locker.addComent(filip, "Do you have an example.");
		locker.addComent(dirk, "Sure, here it is!");
		
		
		
		store.add(zk);
		store.add(world);
		store.add(locker);
		*/
		
	}

}
