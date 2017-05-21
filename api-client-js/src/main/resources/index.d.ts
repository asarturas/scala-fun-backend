export declare class Video {
    title: string;
    url: string;
}

export declare class Client {
    constructor(baseUrl?: string);
    next(): Promise<Video>;
}