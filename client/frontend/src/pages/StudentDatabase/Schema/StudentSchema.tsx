import { z } from 'zod';

export const StudentSchema = z.object({
    id: z.number(),
    firstName: z.string(),
    lastName: z.string(),
    email: z.string(),
})
export type Student = z.infer<typeof StudentSchema>