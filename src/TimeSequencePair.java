package src;

public class TimeSequencePair implements Comparable<TimeSequencePair> {
	public double time_;
	public int Sequence;
	public TimeSequencePair(double t, int seq){
		time_=t;
		Sequence=seq;
	}
	
	@Override
    public int compareTo(TimeSequencePair o) {
        int score;
        if (time_>o.time_){
        	score=1;
        }
        else if (time_==o.time_){
        	score=0;
        }
        else{
        	score=-1;
        }
		return score;
    }
}
