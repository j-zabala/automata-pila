package automata;

import java.util.Set;
import java.util.Stack;
import java.util.*;
import utils.Quintuple;

public final class DFAPila extends AP{

	private   Object nroStates[] ;
    private Stack<Character> stack; //the stack of the automaton


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
    public DFAPila(
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
        if (!rep_ok()){
            throw new  IllegalArgumentException();
        }
        System.out.println("Is a DFA Pila");
    }


		@Override
		public State delta(State from, Character c){
		//TODO this method have to be implemented
			if(stack.empty()){
					return null;
			}
				String aux;
				for(Quintuple<State,Character,Character,String,State> t:transitions){

					if(from.equals(t.first()) && (c.equals(t.second()))  && (stack.peek()==t.third())){
						stack.pop();

						aux=t.fourth();
						for(int i=(((t.fourth()).length())-1);i>=0;i--){
							if(aux.charAt(i)!=Lambda){
								stack.push(aux.charAt(i));
							}

						}
						return t.fifth();
					}
					if(from.equals(t.first()) && (c.equals(t.second())) && (t.third()==Joker)){
						Character top=stack.pop();

						aux=t.fourth();
						for(int i=(((t.fourth()).length())-1);i>=0;i--){
							if(aux.charAt(i)==Joker){
								stack.push(top);
							}
							if((aux.charAt(i)!=Joker) && (aux.charAt(i)!=Lambda)){
								stack.push(aux.charAt(i));
							}

						}
						return t.fifth();
					}

				}
			return null;
		}

		public State deltaEpsilon(State from){
			if(stack.empty()){
				return null;
			}
			State current=from;
			Quintuple<State,Character,Character,String,State> t;
			t=epsilonTransition(current);
			while (t!=null){
				Character c=null;
				if(!stack.empty()){c=stack.pop();
				}else{
					return null;
				}
				String str=t.fourth();
				for(int j=(str.length())-1;j>=0;j--){
					if(str.charAt(j)==Joker && (t.third()==Joker) ){
							stack.push(c);
					}

					if((str.charAt(j)!=Lambda) && (str.charAt(j)!=Joker)){
						stack.push(str.charAt(j));
					}
				}
				if(t!=null){
						current=t.fifth();
				}
				t=epsilonTransition(current);
			}
			return current;

		}

		public Quintuple<State,Character,Character,String,State>  epsilonTransition(State from){
				for(Quintuple<State,Character,Character,String,State> t:transitions){
					if((t.first()).equals(from) && (t.second()==Lambda) && ((!stack.empty() && stack.peek()==t.third())||!stack.empty() &&(t.third()==Joker)) ){
							return t;
				 }
			 }
			 return null;
		}


		@Override
		public boolean accepts(String string){
			if(isFinalState){
				System.out.println();
				System.out.println("ACCEPTS POR ESTADO FINAL");
				System.out.println();
				return acceptsByFinalState(string);
			}else{
				System.out.println();
				System.out.println("ACCEPTS POR PILA VACIA");
				System.out.println();
				return acceptsByEmptyStack(string);
			}
		}


    public boolean acceptsByFinalState(String string) { //aceptacion por estado final
        //TODO this method have to be implemented
			State current = new State(initial.name());
			current.rename(initial.name());
			State aux;


			aux=deltaEpsilon(current);


			if (aux!=null){
				current=aux;
			}else{
				aux=current;
			}

			Quintuple<State,Character,Character,String,State> t;

			for(int i=0; i<(string.length()) && (current!=null); i++){
				t=epsilonTransition(current);


				if(t!=null){


					aux=deltaEpsilon(current);
				}

				aux=delta(current,string.charAt(i));
				if(aux!=null){
					current.rename(aux.name());
				}
				else{
					current=null;
				}
			}
			if((current!=null) && isFinal(current)){
				return true;
			}
    	return false;
    }

		public boolean acceptsByEmptyStack(String string){
			State current = new State(initial.name());
			current.rename(initial.name());
			State aux;


			aux=deltaEpsilon(current);


			if (aux!=null){
				current=aux;
			}else{
				aux=current;
			}

			Quintuple<State,Character,Character,String,State> t;
			int i;
			for(i=0; i<(string.length()) && (current!=null); i++){
				t=epsilonTransition(current);

				if(t!=null){

					aux=deltaEpsilon(current);
				}

				aux=delta(aux,string.charAt(i));

				if(aux!=null){
					current.rename(aux.name());
				}
				else{

					current=null;
				}
			}
			if(current!=null && (i==(string.length()))){
				while (!stackIsEmpty() && current!=null){
					current=delta(current,Lambda);
				}
				if (current!=null){	return true;}

			}
    	return false;
		}

    public boolean rep_ok() {
        //TODO this method have to be implemented
				boolean transitionEpsilon=false;
				for(Quintuple<State,Character,Character,String,State> t:transitions){
					for(Quintuple<State,Character,Character,String,State> l:transitions){

						boolean b1=!(t.equals(l));
						boolean b2=(t.first().equals(l.first()));
						boolean b3=((t.second()).equals(Lambda) && !((l.second()).equals(Lambda)) );
						boolean b4=(!((t.second()).equals(Lambda)) && ((l.second()).equals(Lambda)) );
						boolean b5=(t.second().equals(l.second()));
					//	System.out.println(b1+","+b2+","+b3+","+b4);
						if(b1 && b2 && b3 ){
						//	System.out.println(b1+" && "+b2+" && "+b3);
							System.out.println("EL AUTOMATA ES NO DETERMINISTA. ");
							System.out.println();
							return false;

						}
						if(b1 && b2 && b4){
						//	System.out.println(b1+" && "+b2+" && "+b4);

							System.out.println("EL AUTOMATA ES NO DETERMINISTA. ");
							System.out.println();
							return false;
						}
						if(b1 && b2 && b5 ){
							System.out.println(b1+" && "+b2+" && "+b5);

							System.out.println("EL AUTOMATA ES NO DETERMINISTA. ");
							System.out.println();
							return false;
						}
					}
				}




				boolean reachable=false;
				for (State s:states){
					reachable=false;
					if(!(initial.equals(s))){
						for(Quintuple<State,Character,Character,String,State> t:transitions){
								if(s.equals(t.fifth())){
									reachable=true;
									break;
								}
						}
						if (reachable==false){
							System.out.println("EL AUTOMATA CONTIENE UN ESTADO INALCANZABLE: ");
							System.out.println(s.toString());
							System.out.println();
							return false;
						}

					}
				}

				boolean correctAlphaber=true;
				boolean correctStackAlphabet=true;

				for(Quintuple<State,Character,Character,String,State> t:transitions){
						if(!(alphabet.contains(t.second()))){
							correctAlphaber=false;
							break;
						}
						for(int i=0;i<((t.fourth()).length());i++){
							if(!(stackAlphabet.contains((t.fourth()).charAt(i)))){
								correctStackAlphabet=false;
								break;
							}
						}
						if (correctStackAlphabet==false){
							break;
						}

				}
				if(correctStackAlphabet==false){
					System.out.println("EL AUTOMATA NO RESPETA EL ALFABETO. ");
					System.out.println();
				}
				if(correctAlphaber==false){
					System.out.println("EL AUTOMATA NO RESPETA EL ALFABETO DE LA PILA. ");
					System.out.println();
				}

				return true;


    }

		public boolean isFinal(State s){
			for (State i:finalStates){
				if(s.equals(i)){
					return true;
				}
			}
			return false;
		}

		public boolean stackIsEmpty(){

			if(!stack.empty()){

				if(!(stack.empty()) &&(stack.peek()==stackInitial)){
					stack.pop();
				}
				return ( stack.empty());
			}
			return true;
		}


		public DFAPila stackEmptyToFinalState(){
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

			Quintuple<State,Character,Character,String,State> initialTransitions=new Quintuple<State,Character,Character,String,State>(newInitialState,'_',newStackInitial,""+stackInitial+newStackInitial ,initial);

			newTransitions.add(initialTransitions);

			for(State s:states){
				Quintuple<State,Character,Character,String,State> aux=new Quintuple<State,Character,Character,String,State>(s,'_',newStackInitial,"_",newFinalState);
				newTransitions.add(aux);
			}



			DFAPila pushdownAutomaton=new DFAPila(newStates,newAlphabet,newStackAlphabet,newTransitions,newStackInitial,newInitialState, newFinal_states);

			return pushdownAutomaton;
		}

		public DFAPila finalStateToEmptyStack(){
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
			DFAPila Ap = new DFAPila(newStates,newAlphabet,newStackAlphabet,newTransitions,newStackInitial,newInitialState,newFinal_states);
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
