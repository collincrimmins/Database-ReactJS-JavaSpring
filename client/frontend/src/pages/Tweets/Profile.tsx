import { useState, useEffect, useRef, Dispatch, SetStateAction } from 'react'

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

interface SliceInfo {
    hasNext: boolean,
    lastReadRecordID: number,
}

export default function Profile() {
    const [posts, setPosts] = useState<Post[]>([])
    const [sliceInfo, setSliceInfo] = useState<SliceInfo | null>(null)
    const [loading, setLoading] = useState(false)
    const [userProfiles, setUserProfiles] = useState<Map<Number, User>>(new Map())
    const {user} = useAuthContext()
    const {id} = useParams() // "username" from URL path
    const [cookies] = useCookies();
    const navigate = useNavigate()
    const [writePostText, setWritePostText] = useState("")

    // BUG:
    // Navigate from user2 to user1, and the Page won't reset & refresh correctly.

    // Get Feed on Begin
    useEffect(() => {
        fetchNextProfileFeed()
    }, [])

    // [Posts] Check if new UserProfiles's need to be Fetched
    useEffect(() => {
        fetchListUserInfo()
    }, [posts])

    // [UserProfiles] Apply new UserProfile's to Existing Posts
    useEffect(() => {
        applyUserInfoListToPosts()
    }, [userProfiles])

    // [id] Username has changed - reset page
    useEffect(() => {
        // Reset State
        setSliceInfo(null)
        setUserProfiles(new Map())
        setPosts([])
    }, [id])

    // Get Profile Feed
    async function fetchNextProfileFeed() {
        // Username request is null
        if (id == "") {return}
        // Next Slice does not Exist
        if (sliceInfo) {
            if (!sliceInfo.hasNext) {
                return
            }
        }

        setLoading(true)

        try {
            const username = id
            let body = null;
            if (sliceInfo) {
                body = {lastReadRecordID: sliceInfo.lastReadRecordID}
            }
            const response = await fetch(`http://localhost:8080/posts/profilefeed/${username}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(body)
            })
            const data = await response.json()

            if (!response.ok) {throw new Error()}

            // Get Content & Set Pagination Info
            const list = data.content
            setSliceInfo({
                hasNext: data.hasNext,
                lastReadRecordID: data.lastReadRecordID,
            })

            // Add Local Fields: "username" and "userPhoto"
            list.map((v : Post) => {
                v["username"] = userProfiles.get(v["userID"])?.username || null
                v["userPhoto"] = userProfiles.get(v["userID"])?.photo || null
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
        type bodyList = {
            id: number,
        }
        let body : Array<bodyList> = []

        let requestIDSet = new Set()
        posts.forEach((post) => {
            if (post.username == null) {
                if (!requestIDSet.has(post.userID)) {
                    if (!userProfiles.has(post.userID)) {
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
            setUserProfiles((prevMap : Map<Number, User>) => {
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
                    if (userProfiles.has(v.userID)) {
                        v.username = userProfiles.get(v.userID)!.username
                        v.userPhoto = userProfiles.get(v.userID)!.photo
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

        fetchNextProfileFeed()
    }

    // Load More Posts Button
    function LoadMorePostsButton() {
        return (
            <div className="LoadMoreButton" onClick={LoadMorePostsClick}>
                Load More
            </div>
        )
    }

    // Fetch Post
    async function WritePostSubmitClick(e : React.MouseEvent) {
        e.preventDefault()

        const postText = writePostText
        if (postText == "") {return}

        setLoading(true)

        try {
            // Fetch
            const body = {
                authtoken: user.token,
                text: postText,
            }
            const response = await fetch(`http://localhost:8080/posts/create`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(body)
            })

            const data = await response.json()
            if (!response.ok) {throw new Error()}

            if (data.message == "created-post") {

            }
        } catch {}

        setWritePostText("")
        setLoading(false)
    }

    return (
        <div className = "ProfileLayout">
            <div className="ProfileHeader">
                {id}
            </div>
            <div className="ProfileBody">
                {/* Write Post */}
                {user && user.username == id &&
                    <WritePostBox
                        writePostText={writePostText}
                        setWritePostText={setWritePostText}
                        WritePostSubmitClick={WritePostSubmitClick}
                    />
                }
                {/* Content */}
                {posts.length > 0 && posts.map((post) => {
                    return <PostBox
                        key={post.id}
                        data={post}
                    />
                })}
                {/* Load More */}
                {sliceInfo?.hasNext &&
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

// Write Post Box
type writePostTypes = {
    writePostText: string,
    setWritePostText: Dispatch<SetStateAction<string>>,
    WritePostSubmitClick: any
}
function WritePostBox({writePostText, setWritePostText, WritePostSubmitClick} : writePostTypes) {
    return (
        <div className="WritePostBox">
            <div className="WritePostHeader">
                New Post
            </div>
            <textarea 
                placeholder="Type here..." 
                className="TextArea"
                onChange={(e) => setWritePostText(e.target.value)}
                value={writePostText}
            />
            <button onClick={WritePostSubmitClick} className="ButtonRounded ButtonBlue ButtonOutlineBlack ButtonBold ButtonTextLarge">Submit</button>
        </div>
    )
}