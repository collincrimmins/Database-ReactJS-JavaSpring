import {PulseLoader, MoonLoader} from "react-spinners";
import "../css/App.css"

// Components
type LoadingFrameProps = {
    loading: boolean
}
export const LoadingFrameFullScreen = ({loading} : LoadingFrameProps) => {
    if (loading) {
        return (
            <div className="LoadingFrameFullScreen">
                <MoonLoader
                    color={"#4287f5"}
                    loading={loading}
                    size={25}
                />
            </div>
        )
    }
}

export const LoadingFrameFill = () => {
    return (
        <div className="LoadingFrameFill">
            <MoonLoader 
                className="LoadingFrame"
                color={"#4287f5"}
                // radius={5}
                // height={25}
                // width={10}
                size={25}
                //margin={25}
            />
        </div>
    )
}

// Functions
export const RemoveDuplicates = (array : []) => {
    let NewList : [] = []
    array.forEach((e) => {
        if (!NewList.find((q) => q === e)) {
            NewList.push(e)
        }
    })
    return NewList
}

export const sleep = (ms : number) => {
    // Use this line:
    // await sleep(1000);
    return new Promise(resolve => setTimeout(resolve, ms));
}

export const verifyTextInput = (InputText : string) => {
    if (InputText === "" || !/\S/.test(InputText)) {
        return false
    }
    return true
}