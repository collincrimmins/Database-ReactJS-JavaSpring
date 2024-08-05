import { useState, useEffect, useRef } from 'react'

import { useSearchParams, useNavigate, useLocation, useParams } from 'react-router-dom';

import "../../css/App.css"
import "../../css/Tweets.css"
import { useCookies } from 'react-cookie';
import { useAuthContext } from '../../contexts/useAuthContext';

// Schemas
import { Post, PostSchema } from './Schema/PostSchema.tsx';
import { User } from './Schema/UserSchema.tsx';

export default function Profile() {
    const [posts, setPosts] = useState<Post[]>([
        {id:1, userid:1, text:"this is a textbox", createdAt:"date"},
        {id:2, userid:1, text:"this is a textbox", createdAt:"date"},
        {id:3, userid:1, text:"this is a textbox", createdAt:"date"},
        {id:4, userid:1, text:"this is a textbox", createdAt:"date"}
    ])
    const [users, setUsers] = useState<User[]>([])
    const {user, userAuthToken} = useAuthContext()
    const {id} = useParams()
    const [cookies] = useCookies();
    const navigate = useNavigate()

    useEffect(() => {
        
    }, [])

    // Posts Update: fetch all missing user info
    useEffect(() => {

    }, [posts])

    // Users Update: update posts with user info
    useEffect(() => {

    }, [users])

    // Post
    function PostBox({data} : {data: Post}) {
        return (
            <div className="Post">
                <div className="PostHeader">
                    user id #{data.userid}
                </div>
                <div className="PostBody">
                    {data.text}
                </div>
                <div className="PostFooter">
                    {data.createdAt}
                </div>
            </div>
        )
    }

    return (
        <div className = "ProfileLayout">
            <div className="ProfileHeader">
                {id}
            </div>
            <div className="ProfileBody">
                {posts.map((post) => {
                    return <PostBox
                        key={post.id}
                        data={post}
                    />
                })}
            </div>
        </div>
    )
}