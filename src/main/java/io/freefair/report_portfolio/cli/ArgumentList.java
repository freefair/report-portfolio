package io.freefair.report_portfolio.cli;

import java.util.ArrayList;

public class ArgumentList extends ArrayList<Argument>
{
	public String get(String name)
	{
		Argument result = null;
		for(Argument arg : this){
			if(arg.getName().equalsIgnoreCase(name))
			{
				result = arg;
				break;
			}
		}
		return result != null ? result.getData() : null;
	}
}
