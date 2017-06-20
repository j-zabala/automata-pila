package automata;

import java.util.Set;
import java.util.Stack;
import java.util.*;
import utils.Quintuple;
import utils.Quadruplets;


public final class NFAPila extends AP{

		private   Object nroStates[] ;
    private Stack<Character> stack; //the stack of the automaton
		public static final int numberOfIterations=100;//esto evita que se quede ciclando en caso que el automata posea un ciclo


    /**
     * Constructor of the class - returns a DFAPila object
     * @param states - states of the DFAPila
     * @param alphabet - the alphabet of the automaton
     * @param stackAlphabet - the alphabet of the stack
     * @param transitions - transitions of the automaton
     * @param stackInitial - a Character which represents the initial element of the stack
     * @param initial - initial State of the automaton
     * @param final_states - acceptance states of the automaton
     * @throws IllegalArgumentException
     */
    public NFAPila(
            Set<State> states,
            Set<Character> alphabet,
            Set<Character> stackAlphabet,
            Set<Quintuple<State, Character,Character,String, State>> transitions,
            Character stackInitial,
            State initial,
            Set<State> final_states)
            throws IllegalArgumentException
    {
        this.states = states;
        this.alphabet = alphabet;
        this.stackAlphabet = stackAlphabet;
        stackAlphabet.add(Lambda); //the character lambda is used in the stack to know when do a pop
        stackAlphabet.add(Joker); //the mark of the stack
        this.transitions = transitions;
        this.stackInitial = stackInitial;
        this.initial = initial;
        this.finalStates = final_states;
        nroStates =  states.toArray();
        stack = new Stack<Character>();
				isFinalState=true;
				if(final_states.isEmpty()){
					isFinalState=false;
				}
        stack.add(stackInitial); //insert the mark in the stack
    /*    if (!rep_ok()){
            throw new  IllegalArgumentException();
        }*/
        System.out.println("Is a NFA Pila");

    }


    @Override
    public Set<Quintuple<State,Character,Character,String,State>> delta(State from, Character c){
		//TODO this method have to be implemented
			if(stack.empty()){
					return null;
			}
			Set<Quintuple<State,Character,Character,String,State>> enabledTransitions= new HashSet<Quintuple<State,Character,Character,String,State>>();
			for(Quintuple<State,Character,Character,String,State> t:transitions){
				if(((t.first()).equals(from)) && ( ((t.second()).equals(c)) || ((t.second()).equals(Lambda)) ) && ( ((t.third()).equals(stack.peek())) || ((t.third()).equals(Joker)) )   ){
					enabledTransitions.add(t);
				}
			}
			return enabledTransitions;
		}

		public Quadruplets<State,String,Integer,Stack<Character>> executeTransition(Quadruplets<State,String,Integer,Stack<Character>> config, Quintuple<State,Character,Character,String,State> transition){
			if((config.fourth()).empty()){
				return null;
			}
			Integer i=(config.third());
			if(!(transition.second()).equals(Lambda)){
				 i++;
			}
			Character currentCharacter=(config.fourth()).pop();
			String string=transition.fourth();
			for(int j=((string.length())-1);j>=0;j--){
				if((string.charAt(j)==Joker) && ((transition.third()).equals(Joker))){
					(config.fourth()).push(currentCharacter);
				}
				if((string.charAt(j)!=Lambda)){
					(config.fourth()).push(string.charAt(j));
				}
			}
			Stack<Character> newCurrentStack=(Stack<Character>) (config.fourth()).clone();
			State newCurrentState=new State((transition.fifth()).name());
			Quadruplets<State,String,Integer,Stack<Character>> newCurrenConfig= new Quadruplets<State,String,Integer,Stack<Character>>(newCurrentState,config.second(),i,newCurrentStack);
			return (newCurrenConfig);
		}

		@Override
		public boolean accepts(String string){
			int i=0;
			Quadruplets<State,String,Integer,Stack<Character>> initialConfig= new Quadruplets<State,String,Integer,Stack<Character>>(initial,string,0,stack);
			LinkedList<Quadruplets<State,String,Integer,Stack<Character>>> availableConfigurations=new LinkedList<Quadruplets<State,String,Integer,Stack<Character>>>();
			getConfigurarions(initialConfig,availableConfigurations);
			if(availableConfigurations==null){
				return false;
			}
			while((!availableConfigurations.isEmpty()) && i<(numberOfIterations-1)){
				Quadruplets<State,String,Integer,Stack<Character>> currentConfiguration=availableConfigurations.remove();
				stack=currentConfiguration.fourth();
				if((currentConfiguration!=null) && ((currentConfiguration.third()).equals((((currentConfiguration.second()).length()))))){

					if(isFinalState){
					 	if(isFinal((currentConfiguration.first()),finalStates)){
							return true;
						}
					}else{
						if(stackIsEmpty(currentConfiguration.fourth())){
							return true;
						}
					}
				}
				getConfigurarions(currentConfiguration,availableConfigurations);
				i++;
			}
			return false;
		}


    public LinkedList<Quadruplets<State,String,Integer,Stack<Character>>> getConfigurarions(Quadruplets<State,String,Integer,Stack<Character>> config, LinkedList<Quadruplets<State,String,Integer,Stack<Character>>> configs) { //aceptacion por estado final
        //TODO this method have to be implemented
			State current=config.first();
			Set<Quintuple<State,Character,Character,String,State>> transitionsAvailable=null;
			if(((config.second()).length())>(config.third())){
				transitionsAvailable=delta(current,(config.second()).charAt(config.third()));
			}
			else{
				transitionsAvailable=delta(current,'_');
			}
			if(transitionsAvailable==null){
				return null;
			}

			for(Quintuple<State,Character,Character,String,State> t:transitionsAvailable){
				Quadruplets<State,String,Integer,Stack<Character>> aux=executeTransition(config,t);
				if(aux!=null){configs.add(aux);}
			}
			return configs;
    }


    public boolean rep_ok() {//invariante...estado inalcanzable, tran lambda, pila y lo q contiene el estado esta dentro de tu automata..siempre t moves por lo del alfabeto
        //TODO this method have to be implemented
				return true;
    }

		public boolean isFinal(State s,Set<State> sts){
			for (State i:sts){
				if(s.equals(i)){
					return true;
				}
			}
			return false;
		}

		public boolean stackIsEmpty(Stack<Character> stk){
			if(!stack.empty()){
				if(!(stack.empty()) &&(stack.peek()==stackInitial)){
					stack.pop();
				}
				return ( stack.empty());
			}
			return true;
		}


		public NFAPila stackEmptyToFinalState(){
			Set<State> newStates=new HashSet<State>();
			for(State s:states){
				State aux=new State(s.name());
				newStates.add(aux);
			}
			State newInitialState=new State("newInitialState");
			newStates.add(newInitialState);



			Set<Quintuple<State, Character,Character,String,State>> newTransitions=new HashSet<Quintuple<State, Character,Character,String,State>>();
			for(Quintuple<State,Character,Character,String,State> t:transitions){
				Quintuple<State,Character,Character,String,State> aux=new Quintuple<State,Character,Character,String,State>(t.first(),t.second(),t.third(),t.fourth(),t.fifth());
				newTransitions.add(aux);
			}

			Character newStackInitial='&';

			Set<Character> newAlphabet=new HashSet<Character>();
			for(Character c:alphabet){
				Character aux=new Character(c);
				newAlphabet.add(aux);
			}


			Set<Character> newStackAlphabet=new HashSet<Character>();
			for(Character c:stackAlphabet){
				Character aux=new Character(c);
				newStackAlphabet.add(aux);
			}

			newStackAlphabet.add(newStackInitial);

			Set<State> newFinal_states=new HashSet<State>();
			State newFinalState=new State("newFinalState");
			newFinal_states.add(newFinalState);

			newStates.add(newFinalState);

			Quintuple<State,Character,Character,String,State> initialTransitions=new Quintuple<State,Character,Character,String,State>(newInitialState,'_',newStackInitial,""+newStackInitial+stackInitial,initial);

			newTransitions.add(initialTransitions);

			for(State s:states){
				Quintuple<State,Character,Character,String,State> aux=new Quintuple<State,Character,Character,String,State>(s,'_',newStackInitial,"_",newFinalState);
				newTransitions.add(aux);
			}


			NFAPila pushdownAutomaton=new NFAPila(newStates,newAlphabet,newStackAlphabet,newTransitions,newStackInitial,newInitialState, newFinal_states);

			return pushdownAutomaton;
		}

		public NFAPila finalStateToEmptyStack(){
			Set<State> newStates=new HashSet<State>();
			for(State s:states){
				State aux=new State(s.name());
				newStates.add(aux);
			}
			State newInitialState=new State("newInitialState");
			newStates.add(newInitialState);

			State emptyStackState=new State("emptyStackState");

			newStates.add(emptyStackState);

			Set<Quintuple<State, Character,Character,String,State>> newTransitions=new HashSet<Quintuple<State, Character,Character,String,State>>();
			for(Quintuple<State,Character,Character,String,State> t:transitions){
				Quintuple<State,Character,Character,String,State> aux=new Quintuple<State,Character,Character,String,State>(t.first(),t.second(),t.third(),t.fourth(),t.fifth());
				newTransitions.add(aux);
			}

			Character newStackInitial='&';

			Set<Character> newAlphabet=new HashSet<Character>();
			for(Character c:alphabet){
				Character aux=new Character(c);
				newAlphabet.add(aux);
			}


			Set<Character> newStackAlphabet=new HashSet<Character>();
			for(Character c:stackAlphabet){
				Character aux=new Character(c);
				newStackAlphabet.add(aux);
			}

			newStackAlphabet.add(newStackInitial);

			Set<State> newFinal_states=new HashSet<State>();

			Quintuple<State,Character,Character,String,State> initialTransitions=new Quintuple<State,Character,Character,String,State>(newInitialState,'_',newStackInitial,""+ stackInitial+ newStackInitial,initial);

			newTransitions.add(initialTransitions);

			for(State s:finalStates){
					Quintuple<State,Character,Character,String,State> aux=new Quintuple<State,Character,Character,String,State>(s,'_',Joker,"_",emptyStackState);
					newTransitions.add(aux);
			}

			Quintuple<State,Character,Character,String,State> aux=new Quintuple<State,Character,Character,String,State>(emptyStackState,'_',Joker,"_",emptyStackState);
			newTransitions.add(aux);
			NFAPila Ap = new NFAPila(newStates,newAlphabet,newStackAlphabet,newTransitions,newStackInitial,newInitialState,newFinal_states);
			return Ap;

		}

		public String toString(){
			String type;
			if(isFinalState){
				 type="ESTADO FINAL";
			}else{
				 type="PILA  VACIA";
			}
			System.out.println("ESTE AUTOMATA POR "+type+" ESTA FORMADO POR: ");
			System.out.println("ESTADOS:");
			for(State s:states){
				System.out.print("-");
				System.out.print(s.name());
				System.out.println();

			}

			System.out.println("ALFABETO:");
			for(Character c:alphabet){
				System.out.print("-");
				System.out.print(c);
				System.out.println();

			}

			System.out.println("ALFABETO DEL STACK:");
			for(Character c:stackAlphabet){
				System.out.print("-");
				System.out.print(c);
				System.out.println();

			}

			System.out.println("TRANSICIONES:");
			for(Quintuple<State,Character,Character,String,State> t:transitions){
			  System.out.print("-");
				System.out.print(t.first()+"->"+t.fifth()+" ["+t.second()+"/"+t.third()+"/"+t.fourth()+"].");
				System.out.println();

			}

			if(isFinalState){
				System.out.println("ESTADOS FINALES:");
				for(State s:finalStates){
					System.out.print("-");
					System.out.print(s.name());
					System.out.println();

				}}

				System.out.println("ESTADO INICIAL:");
				System.out.print("-");
				System.out.print(initial.name());
				System.out.println();


				System.out.println("CARACTER INICIAL DEL STACK:");
				System.out.print("-");
				System.out.print(stackInitial);
				System.out.println();
				System.out.println();


					return "OK";
		}


	}
