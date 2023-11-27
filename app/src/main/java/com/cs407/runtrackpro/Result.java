package com.cs407.runtrackpro;

import java.util.List;

public class Result {
    private List<String> destination_addresses;
    private List<String> origin_addresses;
    private List<Rows> rows;
    private String status;

    public void setDestination_addresses(List<String> destination_addresses){
        this.destination_addresses =destination_addresses;
    }

    public List<String> getDestination_addresses(){
        return this.destination_addresses;
    }

    public void setOrigin_addresses(List<String> origin_addresses){
        this.origin_addresses =origin_addresses;
    }
    public List<String> getOrigin_addresses(){
        return this.origin_addresses;
    }
    public void setRows(List<Rows> rows){
        this.rows =rows;
    }
    public List<Rows> getRows(){
        return this.rows;
    }
    public void setStatus(String status){
        this.status =status;
    }
    public String getStatus(){
        return this.status;
    }

    public class Distance{
        private String text;
        private int value;
        public void setText(String text){
            this.text =text;
        }
        public String getText(){return this.text;}
        public void setValue(int value){
            this.value =value;
        }
        public int getValue(){
            return this.value;
        }
    }

    public class Duration{
        private String text;
        private int value;
        public void setText(String text){
            this.text =text;
        }
        public String getText(){
            return this.text;
        }
        public void setValue(int Value){
            this.value =value;
        }
        public int getValue(){
            return this.value;
        }
    }

    public class Elements{
        private Distance distance;
        private Duration duration;
        private String status;
        public void setDistance(Distance distance){
            this.distance =distance;
        }
        public Distance getDistance(){
            return this.distance;
        }
        public void setDuration(Duration duration){
            this.duration =duration;
        }
        public Duration getDuration(){
            return this.duration;
        }
        public void setStatus(String status){
            this.status =status;
        }
        public String getStatus(){
            return this.status;
        }
    }
    public class Rows{
        private List<Elements> elements;
        public void setElements(List<Elements> elements){
            this.elements =elements;
        }
        public List<Elements> getElements(){
            return this.elements;
        }
    }
}
