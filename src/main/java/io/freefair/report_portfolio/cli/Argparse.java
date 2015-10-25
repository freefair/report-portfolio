package io.freefair.report_portfolio.cli;

import java.util.ArrayList;
import java.util.List;

public class Argparse
{
	private String[] args;

	public Argparse(String[] args)
	{
		this.args = args;
	}

	public ArgumentList parse() {
		ArgumentList result = new ArgumentList();
		Argument currentArg = null;
		for(String arg : args){
			if(arg.startsWith("-"))
			{
				currentArg = new Argument();
				currentArg.setName(removeTrailingDashes(arg));
				result.add(currentArg);
			}
			else
			{
				if(currentArg == null)
					throw new RuntimeException("Wrong arguments");
				currentArg.setData(arg);
			}
		}
		return result;
	}

	private String removeTrailingDashes(String src){
		while(src.charAt(0) == '-')
			src = src.substring(1);
		return src;
	}
}
