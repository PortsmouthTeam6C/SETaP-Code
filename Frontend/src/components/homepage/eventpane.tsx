import {CiLocationOn, CiShoppingTag} from "react-icons/ci";
import type {SocietyResponse} from "../../pages/home.tsx";

export interface EventResponse {
    eventId: number,
    userId: number,
    date: Date,
    location: string,
    name: string,
    description: string,
    price: number,
    image: string
}

const events: EventResponse[] = [
    {
        eventId: 1,
        userId: 1,
        date: new Date('2023-11-01T10:00:00'),
        location: "Auditorium A",
        name: "Chess Tournament",
        description: "Compete in our annual chess tournament.",
        price: 10,
        image: "https://images.pexels.com/photos/260024/pexels-photo-260024.jpeg"
    },
    {
        eventId: 2,
        userId: 1,
        date: new Date('2023-11-05T14:00:00'),
        location: "Room 202",
        name: "Debate Workshop",
        description: "Learn the art of debating with experts.",
        price: 5.32,
        image: "https://images.pexels.com/photos/8344905/pexels-photo-8344905.jpeg"
    },
    {
        eventId: 3,
        userId: 1,
        date: new Date('2023-11-10T16:00:00'),
        location: "Photography Studio",
        name: "Photography Basics",
        description: "A beginner's guide to photography techniques.",
        price: 0,
        image: "https://images.pexels.com/photos/733853/pexels-photo-733853.jpeg"
    }
]

function EventCard({ event }: { event: EventResponse }) {
    const { image, date, name, description, location, price } = event;
    const month = date.toLocaleString('default', { month: 'short' }).toUpperCase();

    return <article className={'bg-neutral-50 w-full h-96 shadow-lg rounded-2xl'}>
        <img className={'h-1/2 w-full object-cover shadow-lg rounded-2xl'}
             src={image}
             alt={name} />
        <div className={' h-1/2 w-full flex items-center py-3'}>
            <div className={'w-1/3 h-full flex flex-col items-center border-r-2'}>
                <p className={'text-6xl font-bold'}>{date.getDate()}</p>
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

export function EventsPane({ society }: { society: SocietyResponse } ) {
    // Todo: get events based on society
    return <aside className={'bg-neutral-100 min-w-96 w-96 max-w-96 h-full space-y-4 overflow-scroll p-3'}>
        {events.map((event, i) => <EventCard key={i} event={event} />)}
    </aside>
}
