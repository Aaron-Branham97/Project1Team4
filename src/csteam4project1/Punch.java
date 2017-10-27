package csteam4project1;
import java.util.*;

public class Punch {
    private final int MINUTES_IN_HOUR = 60;
	private int punchID;
	private int terminalID;
	private String badgeID; 

	private GregorianCalendar originalTimestamp;
	private int eventTypeID;
	private String eventData;
	private GregorianCalendar adjustedTimestamp;
    private String adjustmentType;

	
    public Punch(String badgeID, int terminalID, int eventTypeID){
        this.punchID = 0;
        this.terminalID = terminalID;
        this.badgeID = badgeID;
        this.originalTimestamp = new GregorianCalendar();
        this.eventTypeID = eventTypeID;
        this.eventData = null;
        this.adjustedTimestamp = new GregorianCalendar();
        adjustmentType = "";
    }
        
	public Punch (int punchID, int terminalID, String badgeID, long 
				 originalTimestamp, int eventTypeID, String 
				 eventData,long adjustedTimestamp) {
		this.punchID = punchID;
		this.terminalID = terminalID;
		this.badgeID = badgeID;
		this.originalTimestamp = new GregorianCalendar();
		this.originalTimestamp.setTimeInMillis(originalTimestamp);
		this.eventTypeID = eventTypeID;
		this.eventData = eventData;
		this.adjustedTimestamp = new GregorianCalendar();
		this.adjustedTimestamp.setTimeInMillis(adjustedTimestamp);
        adjustmentType = "";
	}

    public int getPunchID() {
        return punchID;
    }

    public void setPunchID(int punchID) {
        this.punchID = punchID;
    }

    public int getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(int terminalID) {
        this.terminalID = terminalID;
    }

    public String getBadgeID() {
        return badgeID;
    }

    public void setBadgeID(String badgeID) {
        this.badgeID = badgeID;
    }

    public GregorianCalendar getOriginalTimestamp() {
        return originalTimestamp;
    }

    public void setOriginalTimestamp(GregorianCalendar originalTimestamp) {
        this.originalTimestamp = originalTimestamp;
    }

    public int getEventTypeID() {
        return eventTypeID;
    }

    public void setEventTypeID(int eventTypeID) {
        this.eventTypeID = eventTypeID;
    }

    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    public GregorianCalendar getAdjustedTimestamp() {
        return adjustedTimestamp;
    }

    public void setAdjustedTimestamp(GregorianCalendar adjustedTimestamp) {
        this.adjustedTimestamp = adjustedTimestamp;
    }

    private String getEventType(int eventTypeID) {
        if (eventTypeID == 0) return "CLOCKED OUT: ";
        else if (eventTypeID == 1) return "CLOCKED IN: ";
        else return "TIMED OUT: ";
    }
    
    private String padInt(int padding) {
        if (padding < 10) return "0" + padding;
        else return padding + "";
    }
        
    private String correctDOW(String DOW){
        String correctDOW = DOW.toUpperCase();
        return correctDOW;
    }
    
    public void adjust(Shift s){
        GregorianCalendar start = new GregorianCalendar(originalTimestamp.get(Calendar.YEAR), originalTimestamp.get(Calendar.MONTH), originalTimestamp.get(Calendar.DAY_OF_MONTH), s.getStart().getHours(), s.getStart().getMinutes());
        GregorianCalendar stop = new GregorianCalendar(originalTimestamp.get(Calendar.YEAR), originalTimestamp.get(Calendar.MONTH), originalTimestamp.get(Calendar.DAY_OF_MONTH), s.getStop().getHours(), s.getStop().getMinutes());
        GregorianCalendar lunchStart = new GregorianCalendar(originalTimestamp.get(Calendar.YEAR), originalTimestamp.get(Calendar.MONTH), originalTimestamp.get(Calendar.DAY_OF_MONTH), s.getLunchstart().getHours(), s.getLunchstart().getMinutes());
        GregorianCalendar lunchStop = new GregorianCalendar(originalTimestamp.get(Calendar.YEAR), originalTimestamp.get(Calendar.MONTH), originalTimestamp.get(Calendar.DAY_OF_MONTH), s.getLunchstop().getHours(), s.getLunchstop().getMinutes());
        
        if(originalTimestamp.get(Calendar.DAY_OF_WEEK) != 7){
            if(originalTimestamp.get(Calendar.DAY_OF_WEEK) != 1){
                if(eventTypeID == 1){
                    if(originalTimestamp.getTimeInMillis() > lunchStart.getTimeInMillis()){
                        adjustedTimestamp.setTimeInMillis(lunchStop.getTimeInMillis());
                        adjustmentType = " (Lunch Stop)";
                    }
                    else if((originalTimestamp.get(Calendar.MINUTE) <= (start.get(Calendar.MINUTE) + s.getGraceperiod())) && (originalTimestamp.get(Calendar.HOUR_OF_DAY) == start.get(Calendar.HOUR_OF_DAY))){
                        adjustedTimestamp.setTimeInMillis(start.getTimeInMillis());
                        adjustmentType = " (Shift Start)";
                    }
                    else if((originalTimestamp.get(Calendar.MINUTE) >= (start.get(Calendar.MINUTE) + (MINUTES_IN_HOUR - s.getDock())))  && (originalTimestamp.get(Calendar.HOUR_OF_DAY) == start.get(Calendar.HOUR_OF_DAY) -1)){
                        adjustedTimestamp.setTimeInMillis(start.getTimeInMillis());
                        adjustmentType = " (Shift Start)";
                    }
                    else if((originalTimestamp.getTimeInMillis() > (start.get(Calendar.MINUTE) + s.getGraceperiod())) && (originalTimestamp.getTimeInMillis() <= start.get(Calendar.MINUTE) + s.getDock())){
                        adjustedTimestamp.setTimeInMillis(start.getTimeInMillis());
                        adjustedTimestamp.roll(Calendar.MINUTE, s.getDock());
                        adjustmentType = " (Shift Start)";
                    }
                    else{
                        adjustedTimestamp.setTimeInMillis(originalTimestamp.getTimeInMillis());
                        adjustedTimestamp.set(Calendar.SECOND, 0);
                        if(originalTimestamp.get(Calendar.MINUTE) % s.getInterval() != 0){
                            if(originalTimestamp.get(Calendar.MINUTE) % s.getInterval() <= s.getInterval()/2){
                                adjustedTimestamp.set(Calendar.MINUTE, originalTimestamp.get(Calendar.MINUTE) - originalTimestamp.get(Calendar.MINUTE) % s.getInterval());
                                adjustmentType = " (Shift Start)";
                            }
                            else{
                                adjustedTimestamp.set(Calendar.MINUTE, originalTimestamp.get(Calendar.MINUTE) + (s.getInterval() - originalTimestamp.get(Calendar.MINUTE) % s.getInterval()));
                                adjustmentType = " (Shift Start)";
                            }
                        }
                        else {
                            adjustedTimestamp.setTimeInMillis(originalTimestamp.getTimeInMillis());
                            adjustedTimestamp.set(Calendar.SECOND, 0);
                            adjustmentType = " (Shift Start)";
                        }
                    }

                }
                else if(eventTypeID == 0 && originalTimestamp.get(Calendar.HOUR) > stop.get(Calendar.HOUR)){
                    adjustedTimestamp.setTimeInMillis(originalTimestamp.getTimeInMillis());
                    adjustmentType = " (None)";
                }
                else if(eventTypeID == 0){
                    if(originalTimestamp.getTimeInMillis() < lunchStop.getTimeInMillis()){
                        adjustedTimestamp.setTimeInMillis(lunchStart.getTimeInMillis());
                        adjustmentType = " (Lunch Start)";
                    }
                    else if((originalTimestamp.get(Calendar.MINUTE) >= (MINUTES_IN_HOUR - s.getGraceperiod())) && (originalTimestamp.get(Calendar.HOUR_OF_DAY) == stop.get(Calendar.HOUR_OF_DAY) - 1)){
                        adjustedTimestamp.setTimeInMillis(stop.getTimeInMillis());
                        adjustmentType = " (Shift Stop)";
                    }
                    else if((originalTimestamp.get(Calendar.MINUTE) <= (stop.get(Calendar.MINUTE) + s.getDock())) && (originalTimestamp.get(Calendar.HOUR_OF_DAY) == stop.get(Calendar.HOUR_OF_DAY))){
                        adjustedTimestamp.setTimeInMillis(stop.getTimeInMillis());
                        adjustmentType = " (Shift Stop)";
                    }
                    else if((originalTimestamp.getTimeInMillis() < (MINUTES_IN_HOUR - s.getGraceperiod())) && (originalTimestamp.getTimeInMillis() >= (MINUTES_IN_HOUR - s.getDock()))){
                        adjustedTimestamp.setTimeInMillis(stop.getTimeInMillis());
                        adjustedTimestamp.roll(Calendar.MINUTE, MINUTES_IN_HOUR - s.getDock());
                        adjustmentType = " (Shift Stop)";
                    }
                    else{
                        if(originalTimestamp.get(Calendar.MINUTE) % s.getInterval() != 0){
                            if(originalTimestamp.get(Calendar.MINUTE) % s.getInterval() <= 7){
                                adjustedTimestamp.setTimeInMillis(originalTimestamp.getTimeInMillis());
                                adjustedTimestamp.set(Calendar.MINUTE, originalTimestamp.get(Calendar.MINUTE) - originalTimestamp.get(Calendar.MINUTE) % s.getInterval());
                                adjustmentType = " (Shift Stop)";
                            }
                            else{
                                adjustedTimestamp.setTimeInMillis(originalTimestamp.getTimeInMillis());
                                adjustedTimestamp.set(Calendar.MINUTE, originalTimestamp.get(Calendar.MINUTE) + (s.getInterval() - originalTimestamp.get(Calendar.MINUTE) % s.getInterval()));
                                adjustmentType = " (Shift Stop)";
                            }
                        }
                        else {
                            adjustedTimestamp.setTimeInMillis(originalTimestamp.getTimeInMillis());
                            adjustedTimestamp.set(Calendar.SECOND, 0);
                            adjustmentType = " (Shift Stop)";
                        }
                    }
                }
            }
        }
        //this Case is for weekends
        else{
            if(originalTimestamp.get(Calendar.MINUTE) % s.getInterval() != 0){
                adjustedTimestamp.setTimeInMillis(originalTimestamp.getTimeInMillis());
                adjustedTimestamp.set(Calendar.SECOND, 0);
                if(originalTimestamp.get(Calendar.MINUTE) % s.getInterval() <= s.getInterval()/2){
                    adjustedTimestamp.set(Calendar.MINUTE, originalTimestamp.get(Calendar.MINUTE) - originalTimestamp.get(Calendar.MINUTE) % s.getInterval());
                    adjustmentType = " (Interval Round)";
                }
                else{
                    adjustedTimestamp.set(Calendar.MINUTE, originalTimestamp.get(Calendar.MINUTE) + (s.getInterval() - originalTimestamp.get(Calendar.MINUTE) % s.getInterval()));
                    adjustmentType = " (Interval Round)";
                }
            }
            else {
                adjustedTimestamp.setTimeInMillis(originalTimestamp.getTimeInMillis());
                adjustedTimestamp.set(Calendar.SECOND, 0);
                adjustmentType = " (Interval Round)";
            }
        }
    }
    
    public String printOriginalTimestamp() {
        return "#" + badgeID + " " + getEventType(eventTypeID) + correctDOW(originalTimestamp.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()))
        + " " + padInt(originalTimestamp.get(Calendar.MONTH) + 1) + "/" + padInt(originalTimestamp.get(Calendar.DAY_OF_MONTH)) + "/" + originalTimestamp.get(Calendar.YEAR) + 
        " " + padInt(originalTimestamp.get(Calendar.HOUR_OF_DAY)) + ":" + padInt(originalTimestamp.get(Calendar.MINUTE)) + ":" + padInt(originalTimestamp.get(Calendar.SECOND));
    }
        
    public String printAdjustedTimestamp() {
        return "#" + badgeID + " " + getEventType(eventTypeID) + correctDOW(adjustedTimestamp.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()))
        + " " + padInt(adjustedTimestamp.get(Calendar.MONTH) + 1) + "/" + padInt(adjustedTimestamp.get(Calendar.DAY_OF_MONTH)) + "/" + adjustedTimestamp.get(Calendar.YEAR) + 
        " " + padInt(adjustedTimestamp.get(Calendar.HOUR_OF_DAY)) + ":" + padInt(adjustedTimestamp.get(Calendar.MINUTE)) + ":" + padInt(adjustedTimestamp.get(Calendar.SECOND)) + adjustmentType;
    }
}