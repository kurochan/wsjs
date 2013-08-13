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
  (a[0] <=> b[0])
}

result.each do |res|
  puts "#{res[0]}"
end
