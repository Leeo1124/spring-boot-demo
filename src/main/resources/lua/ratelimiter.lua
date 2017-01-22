local result = 0
local key, value, limit, expireTime  = KEYS[1], ARGV[1], ARGV[2], ARGV[3]
local current = redis.call('LLEN', key)

if tonumber(current) >= tonumber(limit) then
	return false
else
	if redis.call('EXISTS', key) == 0 then
		--redis.call('MULTI')
		result = redis.call('RPUSH', key, value)
		if expireTime and result == 1 then
			redis.call('EXPIRE', key, expireTime)
		end
		--redis.call('EXEC')
	else 
		result = redis.call('RPUSHX', key, value)
	end
	
	if result > 0 then
		return true
	else
		return false
	end
end