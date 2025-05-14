import {CiLocationOn, CiShoppingTag} from "react-icons/ci";
import {type SocietyResponse} from "../../api/societies.ts";
import {useContext, useEffect, useState} from "react";
import {type EventResponse, getEvents} from "../../api/events.ts";
import {UserContext} from "../../context/UserContext.ts";

function EventCard({ event }: { event: EventResponse }) {
    const { image, date, name, description, location, price } = event;
    const dateAsDate = new Date(date);
    const month = dateAsDate.toLocaleString('default', { month: 'short' }).toUpperCase();

    return <article className={'bg-neutral-50 w-full h-96 shadow-lg rounded-2xl'}>
        <img className={'h-1/2 w-full object-cover shadow-lg rounded-2xl'}
             src={image}
             alt={name} />
        <div className={' h-1/2 w-full flex items-center py-3'}>
            <div className={'w-1/3 h-full flex flex-col items-center border-r-2'}>
                <p className={'text-6xl font-bold'}>{dateAsDate.getDate().toString().padStart(2, '0')}</p>
                <p className={'text-3xl'}>{month}</p>
            </div>
            <div className={'w-2/3 h-full px-3 flex flex-col justify-between'}>
                <div>
                    <h3 className={'text-3xl font-serif'}>{name}</h3>
                    <p>{description}</p>
                </div>
                <div className={'flex flex-col space-x-2 -translate-y-12'}>
                    <div className={'flex flex-row -space-x-1 items-center relative top-6'}>
                        <CiShoppingTag className={'absolute top-[6px]'} />
                        <p className={'absolute top-0 left-5'}>{price ? `£${price.toFixed(2).replace(/\.00$/, '')}` : 'Free entry'}</p>
                    </div>
                    <div className={'flex flex-row -space-x-1 items-center relative'}>
                        <CiLocationOn className={'absolute top-[6px]'} />
                        <p className={'absolute top-0 left-5'}>{location}</p>
                    </div>
                </div>
            </div>
        </div>
    </article>
}

export function EventsPane({ society }: { society: SocietyResponse|undefined } ) {
    const context = useContext(UserContext);
    const [events, setEvents] = useState<EventResponse[]>([]);
    useEffect(() => {
        if (context.isLoggedIn() && society !== undefined) {
            getEvents(society.societyId)
                .then(response => {
                    if (response === undefined)
                        setEvents([]);
                    else
                        setEvents(response);
                })
        }
    }, [context, society]);
    
    return <aside className={'bg-neutral-100 min-w-96 w-96 max-w-96 max-h-[1180px] space-y-4 overflow-scroll p-3'}>
        {events.map((event, i) => <EventCard key={i} event={event} />)}
    </aside>
}
