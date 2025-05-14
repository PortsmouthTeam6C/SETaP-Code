export default function truncate(length: number, str: string): string {
    if (str.length <= length)
        return str;

    return str.substring(0, length - 3) + '...';
}
