<request>

{$document/request/name}

<user action="retrieve" columns="name,age">
    <name condition="EQ">{data($document/request/name)}</name>
    <pass condition="EQ">{data($document/request/pass)}</pass>
</user>

<login_attempt action="retrieve" columns="attemptCount" key="{data($document/request/name)}_login_attempt" />

<_next>
    <code>success</code>
</_next>
</request>
