hash = Hash::new
hash.default = 0
line = ""

while(line = gets)
  array = line.split
  array.each do |str|
    hash[str] += 1
  end
end

result = hash.to_a.sort{|a, b|
  (b[1] <=> a[1]) * 2 + (a[0] <=> b[0])
}

result.each do |res|
  puts "#{res[0]} #{res[1]}"
end
