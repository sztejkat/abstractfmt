package sztejkat.abstractfmt.util;

/**
	A parser state machine.
	<p>
	Managing even simplest parser can be done in an easy and consistent
	way by using a kind of state-driven parser. This state-driven parser
	decides what kind of <i>element</i> is allowed in what kind of <i>state</i>
	and depending on that element transits to other state or throws an exception
	informing that the syntax is incorrect.
	<p>
	Of course this is possible to parse any format without such a state machine,
	but using state machine allows easy validation of format syntax. This is especially
	usefull if we are using just a part of original format specification but we
	like to be robust against malformed input files. 
	
*/
public abstract class AParserStateMachine
{
}