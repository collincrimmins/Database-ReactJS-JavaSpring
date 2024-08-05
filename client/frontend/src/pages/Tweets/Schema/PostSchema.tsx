import { z } from 'zod';

export const PostSchema = z.object({
    id: z.number(),
    userid: z.number(),
    text: z.string(),
    createdAt: z.string(),
})
export type Post = z.infer<typeof PostSchema>