package utils;

public class Quadruplets <A,B,C,D> {

    // Private attributes
    private A first;
    private B second;
    private C third;
    private D fourth;
    
    
    /**
     * Construct of the class - returns a Quintuple Object
     * @param fst
     * @param snd
     * @param trd
     * @param frt
     * @param fif
     */
    public Quadruplets(A fst, B snd, C trd, D frt) {
        first = fst;
        second = snd;
        third = trd;
        fourth = frt;
    }

    /**
     * Getter method
     * @return the first element of the Quintuple
     */
    public A first() {
        return first;
    }

    /**
     * Getter method
     * @return the second element of the Quintuple
     */
    public B second() {
        return second;
    }

    /**
     * Getter method
     * @return the third element of the Quintuple
     */
    public C third() {
        return third;
    }
    
    public D fourth() {
        return fourth;
    }
   
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        result = prime * result + ((third == null) ? 0 : third.hashCode());
        result = prime * result + ((fourth == null) ? 0 : fourth.hashCode());

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
        Quadruplets other = (Quadruplets) obj;
        if (first == null) {
            if (other.first != null) {
                return false;
            }
        } else if (!first.equals(other.first)) {
            return false;
        }
        if (second == null) {
            if (other.second != null) {
                return false;
            } else {
            }
        } else if (!second.equals(other.second)) {
            return false;
        }
        if (third == null) {
            if (other.third != null) {
                return false;
            }
        } else if (!third.equals(other.third)) {
            return false;
        }if (fourth == null) {
            if (other.fourth != null) {
                return false;
            }
        } else if (!fourth.equals(other.fourth)) {
            return false;
        }
        return true;
    }
    
    public String toString(){
        return "("+ first.toString() + "," + second.toString() + ","+ third.toString() +","+ fourth.toString() +")";
    }
	
	
}
