array = []
str = ""

while(str = gets)
  array.push str
end
array.shuffle!

array.each do |str|
  puts str
end
