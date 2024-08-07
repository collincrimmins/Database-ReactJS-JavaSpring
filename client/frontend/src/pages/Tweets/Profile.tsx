import { useState, useEffect, useRef } from 'react'

import { useSearchParams, useNavigate, useLocation, useParams } from 'react-router-dom';

import "../../css/App.css"
import "../../css/Tweets.css"

import { useCookies } from 'react-cookie';
import { useAuthContext } from '../../contexts/useAuthContext';

// Schemas
import { Post, PostSchema } from './Schema/PostSchema.tsx';
import { User, UserSchema } from './Schema/UserSchema.tsx';

import { LoadingFrameFullScreen } from '../../utils/Library.tsx';
import { formatDistanceToNow } from 'date-fns';

export default function Profile() {
    const [posts, setPosts] = useState<Post[]>([])
    const [slicePageNumber, setSlicePageNumber] = useState(0)
    const [sliceHasNext, setSliceHasNext] = useState(true)
    const [loading, setLoading] = useState(false)
    const [usersInfo, setUsersInfo] = useState<Map<Number, User>>(new Map())
    //const {user, userAuthToken} = useAuthContext()
    const {id} = useParams() // "username" from URL path
    const [cookies] = useCookies();
    const navigate = useNavigate()

    useEffect(() => {
        fetchProfileFeedBySlicePage(null)
    }, [])

    // Posts Update: fetch all missing user info
    useEffect(() => {
        // Check if new UserInfo's need to be Loaded
        fetchListUserInfo()
    }, [posts])

    // Users Update: update posts with user info
    useEffect(() => {
        // Apply new UserInfo to all Posts
        applyUserInfoListToPosts()
    }, [usersInfo])

    // Get Profile Feed
    async function fetchProfileFeedBySlicePage(page : number | null) {
        setLoading(true)

        try {
            // Get Username
            if (id == "") {return}
            const username = id
            // Get Page Number (from ClickToLoadMore or Page Startup)
            const nextRequestPage = page || slicePageNumber
            // Fetch
            const params = new URLSearchParams({
                "pageNumber": nextRequestPage.toString()
            })
            const response = await fetch(`http://localhost:8080/posts/profilefeed/${username}?${params}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            })
            const data = await response.json()

            if (!response.ok) {throw new Error()}

            // Get Content & Set Pagination Info
            const list = data.content
            const listPageNumber = data.pageNumber
            const listHasNext = data.hasNext
            setSlicePageNumber(listPageNumber)
            setSliceHasNext(listHasNext)

            // Add Local Fields: "username" and "userPhoto"
            list.map((v : Post) => {
                v["username"] = usersInfo.get(v["userID"])?.username || null
                v["userPhoto"] = usersInfo.get(v["userID"])?.photo || null
                return v
            })
            
            // Check Data Schema's
            let validListOfSchema = true
            list.forEach((v : Post) => {
                if (!validListOfSchema) {return}
                const validSchema = PostSchema.safeParse(v).success
                if (!validSchema) {
                    validListOfSchema = false
                }
            })
            if (!validListOfSchema) {console.warn("input-error"); throw new Error("input-error")}
            
            // Set Data
            setPosts((prevPosts) => {
                return [...prevPosts, ...list]
            })
        } catch {}

        setLoading(false)
    }

    // Get UserInfoList by UserID from Posts
    async function fetchListUserInfo() {
        setLoading(true)

        // Check if any new UserID needs to be added to the Request
        // Get Body
        type bodyList = {
            id: number,
        }
        let body : Array<bodyList> = []
        let requestIDSet = new Set()
        posts.forEach((post) => {
            if (post.username == null) {
                if (!requestIDSet.has(post.userID)) {
                    if (!usersInfo.has(post.userID)) {
                        body.push({id: post.userID})
                        requestIDSet.add(post.userID)
                    }
                }
            }
        })
        if (requestIDSet.size == 0) {setLoading(false); return}

        try {
            // Fetch
            const response = await fetch(`http://localhost:8080/users/infolist`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(body)
            })
            const data = await response.json()
            if (!response.ok) {throw new Error()}
            
            // Check Data Schema's
            let validListOfSchema = true
            data.forEach((v : User) => {
                if (!validListOfSchema) {return}
                const validSchema = UserSchema.safeParse(v).success
                if (!validSchema) {
                    validListOfSchema = false
                }
            })
            if (!validListOfSchema) {console.warn("input-error"); throw new Error("input-error")}
            
            // Set Data
            setUsersInfo((prevMap : Map<Number, User>) => {
                data.forEach((newUser : User) => {
                    if (!prevMap.has(newUser.id)) {
                        prevMap.set(newUser.id, newUser)
                    }
                })
                return new Map(prevMap)
            })
        } catch {}

        setLoading(false)
    }

    // Apply UserInfo to all Posts
    function applyUserInfoListToPosts() {
        setPosts((prevPosts : Post[]) => {
            prevPosts.forEach((v : Post) => {
                if (v.username == null) {
                    if (usersInfo.has(v.userID)) {
                        v.username = usersInfo.get(v.userID)!.username
                        v.userPhoto = usersInfo.get(v.userID)!.photo
                    }
                }
            })
            return [...prevPosts]
        })
    }

    // PostBox
    function PostBox({data} : {data: Post}) {
        let date = new Date(Date.parse(data.createdDate))
        let timeFormattedClassic = "(" + (date.getMonth() + 1) 
            + "/" + (date.getDate()) + "/" + date.getFullYear() + ")"
        let timeFormatted = formatDistanceToNow(date, {addSuffix: true}) + " " + timeFormattedClassic
        return (
            <div className="Post">
                <div className="PostPhotoBox">
                    <img src = {data.userPhoto || ""} className="ProfilePhoto"/>
                </div>
                <div className="PostMain">
                    <div className="PostHeader">
                            {data.username || data.userID}
                        </div>
                    <div className="PostBody">
                        {data.text}
                    </div>
                    <div className="PostFooter">
                        {timeFormatted}
                    </div>
                </div>
            </div>
        )
    }

    // Load More Posts
    function LoadMorePostsClick(e : React.MouseEvent) {
        e.preventDefault()

        fetchProfileFeedBySlicePage(slicePageNumber + 1)
    }

    // Load More Posts Button
    function LoadMorePostsButton() {
        return (
            <div className="LoadMoreButton" onClick={LoadMorePostsClick}>
                Load More
            </div>
        )
    }

    return (
        <div className = "ProfileLayout">
            <div className="ProfileHeader">
                {id}
            </div>
            <div className="ProfileBody">
                {/* Content */}
                {posts.length > 0 && posts.map((post) => {
                    return <PostBox
                        key={post.id}
                        data={post}
                    />
                })}
                {/* Load More */}
                {sliceHasNext &&
                    <LoadMorePostsButton/>
                }
                {/* Loading */}
                {loading &&
                    <LoadingFrameFullScreen loading={loading}/>
                }
            </div>
        </div>
    )
}