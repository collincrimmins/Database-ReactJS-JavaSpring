import React, {useContext, useState, useEffect} from 'react'

const TestContext = React.createContext({})

export function useTestContext() {
    return useContext(TestContext)
}

type TestProviderProps = {
    children: React.ReactNode
}
export default function TestProvider({children} : TestProviderProps) {
    const contextValues = {
        testfunction
    }

    function testfunction() {

    }

    // Render Loading Frame if Auth State is not Known
    return (
        <TestContext.Provider value={contextValues}>
             {/* {!loading && children} */}
            {children}
        </TestContext.Provider>
    )
}