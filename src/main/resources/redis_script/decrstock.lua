local key = KEYS[1]
local delta = tonumber(ARGV[1])
local ret = tonumber(redis.call('get', key) or '0')
if ret >= delta then
    redis.call('decrby', key, delta)
    return 1
else
    return 0
end