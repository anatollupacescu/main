<request>
    
    {$document/request/name}
    
    <log action="persist" key="user_login_{data($document/request/name)}_{fn:current-dateTime()}" />
    
    {
        let $attemptCount := fn:empty($document/request/login_attempt)
        return if($attemptCount eq true()) then
            <login_attempt action="persist" key="{data($document/request/name)}" >
                <count>1</count>
            </login_attempt>
        else 
            <login_attempt action="persist" key="{data($document/request/name)}" >
                <count>{data($document/request/login_attempt) + 1}</count>
            </login_attempt>
    }
 
    {
        let $userCount := count($document/request/user)
        return if($userCount gt 0)
        then 
            <_next>
                <code>yes</code>
            </_next>
        else
            <_next>
                <code>no</code>
                <stringValue name="error">User/password do not match</stringValue>
            </_next>
    }
</request>
