local result = 0
local key, limit, expireTime  = KEYS[1], ARGV[1], ARGV[2]
local current = redis.call('GET', key)

if current and tonumber(current) >= tonumber(limit) then
	return false
else
	result = redis.call('INCR', key)
	if expireTime and result == 1 then
		redis.call('EXPIRE', key, expireTime)
	end
	
	if result > 0 then
		return true
	else
		return false
	end
end