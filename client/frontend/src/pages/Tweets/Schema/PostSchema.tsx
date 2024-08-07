import { z } from 'zod';

export const PostSchema = z.object({
    id: z.number(),
    userID: z.number(),
    text: z.string(),
    createdDate: z.string(),
    username: z.string().nullable(),
    userPhoto: z.string().nullable(),
})
export type Post = z.infer<typeof PostSchema>