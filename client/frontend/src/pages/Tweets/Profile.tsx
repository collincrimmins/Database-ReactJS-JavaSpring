import { useState, useEffect, useRef, Dispatch, SetStateAction } from 'react'

import { useSearchParams, useNavigate, useLocation, useParams } from 'react-router-dom';

import "../../css/App.css"
import "../../css/Tweets.css"

import { useCookies } from 'react-cookie';
import { useAuthContext } from '../../contexts/useAuthContext';

// Schemas
import { Post, PostSchema } from './Schema/PostSchema.tsx';
import { User, UserSchema } from './Schema/UserSchema.tsx';

import { LoadingFrameFill, LoadingFrameFullScreen, sleep } from '../../utils/Library.tsx';
import Skeleton from 'react-loading-skeleton'
import 'react-loading-skeleton/dist/skeleton.css'
import { formatDistanceToNow } from 'date-fns';

interface SliceInfo {
    hasNext: boolean,
    lastReadRecordID: number,
}

export default function ProfileComponent() {
    const [posts, setPosts] = useState<Post[]>([])
    const [sliceInfo, setSliceInfo] = useState<SliceInfo | null>(null)
    const [userProfiles, setUserProfiles] = useState<Map<Number, User>>(new Map())
    const [writePostText, setWritePostText] = useState("")

    const [loadingProfileHeader, setLoadingProfileHeader] = useState(false)
    const [loadingPageInitial, setLoadingPageInitial] = useState(false)
    const [loadingNextPage, setLoadingNextPage] = useState(false)

    const {user} = useAuthContext()
    const {id} = useParams() // "username" from URL path

    useEffect(() => {
        // Get Feed
        fetchNextProfileFeed()
        // Get UserProfile of the Profile
        fetchProfileUserInfo()
    }, [])

    // [Posts] Check if new UserProfiles's need to be Fetched
    useEffect(() => {
        fetchListUserInfo()
        applyUserProfilesToPosts()
    }, [posts])

    // [UserProfiles] Apply new UserProfile's to Existing Posts
    useEffect(() => {
        applyUserProfilesToPosts()
    }, [userProfiles])

    // [id] Username has changed - reset page
    useEffect(() => {
        // Reset State
        // setSliceInfo(null)
        // setUserProfiles(new Map())
        // setPosts([])
    }, [id])

    // Get UserInfo for this Profile by Username
    async function fetchProfileUserInfo() {
        // Username request is null
        if (id == "") {return}

        setLoadingProfileHeader(true)

        try {
            const params = new URLSearchParams({
                username: id!
            })
            const response = await fetch(`http://localhost:8080/users/info?${params}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            })
            const data = await response.json()
            if (!response.ok) {throw new Error()}
            
            // Check Data Schema's
            const validSchema = UserSchema.safeParse(data).success
            if (!validSchema) {console.warn("input-error"); throw new Error("input-error")}
            
            // Set Data
            const newUserProfile = data
            if (!userProfiles.has(newUserProfile.id)) {
                setUserProfiles((prevMap : Map<Number, User>) => {
                    prevMap.set(newUserProfile.id, newUserProfile)
                    return new Map(prevMap)
                })
            }
        } catch {}

        setLoadingProfileHeader(false)
    }

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

        let loadingType = "initialPage"
        if (!sliceInfo) {
            setLoadingPageInitial(true)
        } else {
            loadingType = "nextPage"
            setLoadingNextPage(true)
        }

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

        if (loadingType == "initialPage") {
            setLoadingPageInitial(false)
        } else {
            setLoadingNextPage(false)
        }
    }

    // Get UserInfoList by UserID from Posts
    async function fetchListUserInfo() {
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
        if (requestIDSet.size == 0) {
            //setLoading(false)
            return
        }

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
    }

    // Apply UserInfo to all Posts
    function applyUserProfilesToPosts() {
        let updatedState = false
        setPosts((prevPosts : Post[]) => {
            prevPosts.forEach((v : Post) => {
                if (v.username == null) {
                    if (userProfiles.has(v.userID)) {
                        v.username = userProfiles.get(v.userID)!.username
                        v.userPhoto = userProfiles.get(v.userID)!.photo
                        updatedState = true
                    }
                }
            })
            if (updatedState) {
                return [...prevPosts]
            } else {
                return prevPosts
            }
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

    function PostBoxLoading() {
        return (
            <div className="PostLoading">
                <Skeleton className="SkeletonFill"/>
            </div>
        )
    }

    // LoadMorePostsClick
    function LoadMorePostsClick(e : React.MouseEvent) {
        e.preventDefault()

        if (loadingNextPage) {return}

        fetchNextProfileFeed()
    }

    // LoadMorePostsButton
    function LoadMorePostsButton() {
        return (
            <div className="LoadMoreButton" onClick={LoadMorePostsClick}>
                Load More
            </div>
        )
    }

    function LoadMorePostsButtonLoading() {
        return (
            <div className="LoadMoreButtonLoading">
                <LoadingFrameFill/>
            </div>
        )
    }

    // WritePostSubmitClick
    async function WritePostSubmitClick(e : React.MouseEvent) {
        e.preventDefault()

        const postText = writePostText
        if (postText == "") {return}

        //setLoading(true)

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
                setWritePostText("")
            }
        } catch {}

        //setLoading(false)
    }

    // Follow Button
    function FollowButton() {
        return (
            <div className="ProfileButton">
                follow
            </div>
        )
    }

    // Profile Header
    function ProfileHeader() {
        const username = id

        let photo
        let profileBackground = "https://i0.wp.com/backgroundabstract.com/wp-content/uploads/edd/2022/02/vecteezywhite-backgroundYK0221_generated-e1656067754363.jpg?resize=150150&ssl=1"
        userProfiles.forEach((v) => {
            if (v.username == username) {
                photo = v.photo
            }
        })

        return (
            <div className="ProfileHeader">
                <div className = "ProfileHeaderBackground">
                    <img src = {profileBackground} className="ProfileHeaderBackgroundPhoto"/>
                </div>
                <div className = "ProfileHeaderMiddle">
                    <img src = {photo} className="ProfilePhotoHeader"/>
                    <div className="ProfileHeaderText">
                        {id}
                    </div>
                    <FollowButton/>
                </div>
                <div className = "ProfileHeaderBottom">
                    <div className="ProfileHeaderBio">
                        Lorem ipsum dolor sit amet, consectetur adipisci elit, 
                        sed eiusmod tempor incidunt ut labore et dolore magna aliqua. 
                        Ut enim ad minim veniam, quis nostrum exercitationem ullam corporis 
                        suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur.
                    </div>
                    <div className="ProfileHeaderSocialInfo">
                        100 Followers | 50 Following
                    </div>
                </div>
            </div>
        )
    }

    function ProfileHeaderLoading() {
        return (
            <div className="ProfileHeaderLoading">
                <Skeleton className="SkeletonFill"/>
            </div>
        )
    }

    return (
        <div className = "ProfileLayout">
            {loadingProfileHeader? 
                <ProfileHeaderLoading/>
                :
                <ProfileHeader/>
            }
            <div className="ProfileBody">
                {/* Write Post */}
                {loadingProfileHeader == false && user && user.username == id &&
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
                {loadingPageInitial && 
                    new Array(5).fill("").map((_, index) => {
                        return <PostBoxLoading key={index} />
                    })
                }
                {/* Load More */}
                {!loadingNextPage && sliceInfo?.hasNext &&
                    <LoadMorePostsButton/>
                }
                {loadingNextPage &&
                    <LoadMorePostsButtonLoading/>
                }
            </div>
        </div>
    )
}

// WritePostBox (Input must be outside of the render, so the input-focus will not be lost)
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