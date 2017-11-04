package org.daisy.dotify.cli;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.api.obfl.Expression;
import org.daisy.dotify.consumer.obfl.ExpressionFactoryMaker;
import org.daisy.streamline.cli.AbstractUI;
import org.daisy.streamline.cli.Argument;
import org.daisy.streamline.cli.CommandParserResult;
import org.daisy.streamline.cli.ExitCode;
import org.daisy.streamline.cli.OptionalArgument;
import org.daisy.streamline.cli.SwitchArgument;

public class EvaluateCLI extends AbstractUI {
	private final static String META_KEY = "meta";
	private final static String HELP_KEY = "help";
	private final List<Argument> reqArgs;
	private final List<OptionalArgument> optionalArgs;

	public EvaluateCLI() {
		this.reqArgs = new ArrayList<Argument>();
		this.optionalArgs = new ArrayList<OptionalArgument>();
		parser.addSwitch(new SwitchArgument('h', HELP_KEY, META_KEY, HELP_KEY, "Help text."));
	}
	
	public static void main(String[] args) throws IOException {
		EvaluateCLI m = new EvaluateCLI();
		CommandParserResult result = m.parser.parse(args);
		if (HELP_KEY.equals(result.getOptional().get(META_KEY))) {
			m.displayHelp(System.out);
			EvaluateCLI.exitWithCode(ExitCode.OK);
		} else {
			m.runCLI(result);
		}
	}
	
	private void runCLI(CommandParserResult cmd) throws IOException {
		ExpressionFactoryMaker efm = ExpressionFactoryMaker.newInstance();
		Expression exp = efm.getFactory().newExpression();
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(System.in));
		String text;
		System.out.print("> ");
		while ((text=lnr.readLine())!=null) {
			if ("quit".equalsIgnoreCase(text) || "exit".equalsIgnoreCase(text)) {
				break;
			}
			try {
				System.out.println(exp.evaluate(text));
			} catch (Exception e) {
				System.out.println("An error occured: " + e.getLocalizedMessage());
			}
			System.out.print("> ");
		}
	}

	@Override
	public String getDescription() {
		return "Evaluates expressions interactively";
	}

	@Override
	public String getName() {
		return "evaluate";
	}

	@Override
	public List<OptionalArgument> getOptionalArguments() {
		return optionalArgs;
	}

	@Override
	public List<Argument> getRequiredArguments() {
		return reqArgs;
	}

}
