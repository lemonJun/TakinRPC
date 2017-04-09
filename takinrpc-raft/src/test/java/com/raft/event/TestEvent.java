package com.raft.event;

/**
 * Created by Administrator on 2016/11/3.
 */
public class TestEvent {
    public static void main(String[] args) {
        EventContext context = new StandardEventManager(10);
        context.addListener(PersonEvent.Person_Event, new PersonTransport());

        context.fireAsyncEvent(PersonEvent.Person_Event,PersonStatus.eat);
        context.fireAsyncEvent(PersonEvent.Person_Event,PersonStatus.go);
    }
}

class PersonTransport implements EventListener<PersonStatus>
{
    public void onEvent(String event, PersonStatus eventData) throws Throwable {
        if(eventData == PersonStatus.eat)
        {
            System.out.println("doEat!");
        }
        else if(eventData == PersonStatus.go)
        {
            System.out.println("doGo!");
        }
    }
}

interface PersonEvent
{
    String Person_Event = "Person_Event";
}

enum PersonStatus
{
    go,eat
}